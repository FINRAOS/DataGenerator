package org.finra.datagenerator.scaffolding.transformer.service.adapter;

import com.google.common.io.Files;
import org.finra.datagenerator.scaffolding.transformer.utils.ByteClassLoader;
import org.finra.datagenerator.scaffolding.utils.ClassInfo;
import org.finra.datagenerator.scaffolding.utils.Javac;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dkopel on 10/28/16.
 */

public class XSDAdapter implements TransformerAdapter {

    private List<String> generatePOJO(Path output, Path input) throws Exception {
        String exec = String.format("/usr/bin/xjc -d %s %s", output, input);
        System.out.println(exec);
        Process p = Runtime.getRuntime().exec(exec);
        p.waitFor();

        Path generated = Paths.get(output.toString()+"/generated");
        File[] files = generated.toFile().listFiles();
        List<String> sourceFiles = Arrays.stream(files).map(f -> f.toString()).collect(Collectors.toList());
        return sourceFiles.stream().map((String sf) -> {
            System.out.println("Source: "+sf);
            try {
                return new String(java.nio.file.Files.readAllBytes(Paths.get(sf)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    private List<Class<?>> compile(List<String> sources) throws IOException {
        List<ClassInfo> classInfos = Javac.compile(sources);
        Map<String, byte[]> classMap = classInfos.stream().collect(Collectors.toMap(
            (ClassInfo c) -> c.className,
            (ClassInfo c) -> c.bytes
        ));
        ByteClassLoader cl = new ByteClassLoader(new URL[] {}, ClassLoader.getSystemClassLoader(), classMap);

        return classInfos.stream().map((ClassInfo c) -> {
            try {
                return cl.loadClass(c.className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> convert(Path input) {
        Path tmpDir = Files.createTempDir().toPath();
        try {
            List<String> sources = generatePOJO(tmpDir, input);
            return compile(sources);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
