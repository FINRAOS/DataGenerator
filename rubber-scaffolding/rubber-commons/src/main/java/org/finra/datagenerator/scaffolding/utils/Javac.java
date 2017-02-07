package org.finra.datagenerator.scaffolding.utils;

/**
 * Created by dkopel on 10/28/16.
 */

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple utility class to invoke the java compiler.
 */

public class Javac {
    public static List<ClassInfo> compile(List<String> srcCodes) throws IOException {

        List<SourceInfo> srcInfos = getSourceInfos(srcCodes);

        File rootDir = getTmpDir(); // something like "/tmp/kilim$2348983948"

        File classDir = new File(rootDir.getAbsolutePath() + File.separatorChar + "classes");
        classDir.mkdir(); // "<rootDir>/classes"

        String options[] = { "-d", classDir.getAbsolutePath() };

        String args[] = new String[options.length + srcCodes.size()];
        System.arraycopy(options, 0, args, 0, options.length);
        int i = options.length;

        for (SourceInfo srci : srcInfos) {
            String name = rootDir.getAbsolutePath() + File.separatorChar + srci.className + ".java";
            writeFile(new File(name), srci.srcCode.getBytes());
            args[i++] = name;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, args);

        List<ClassInfo> ret = new ArrayList<ClassInfo>();
        addClasses(ret, "", classDir);
        deleteDir(rootDir);
        return ret;
    }

    private static List<SourceInfo> getSourceInfos(List<String> srcCodes) {
        List<SourceInfo> srcInfos = new ArrayList<SourceInfo>(srcCodes.size());
        for (String srcCode : srcCodes) {
            srcInfos.add(getSourceInfo(srcCode));
        }
        return srcInfos;
    }

    static Pattern publicClassNameRegexp = Pattern.compile("public +(?:class|interface) +(\\w+)");
    static Pattern classNameRegexp = Pattern.compile("(?:class|interface) +(\\w+)");

    private static SourceInfo getSourceInfo(String srcCode) {
        Matcher m = publicClassNameRegexp.matcher(srcCode);
        if (m.find())
            return new SourceInfo(m.group(1), srcCode);
        else {
            m = classNameRegexp.matcher(srcCode);
            if (m.find())
                return new SourceInfo(m.group(1), srcCode);
            else
                throw new IllegalArgumentException(
                    "No class or interface definition found in src: \n'" + srcCode + "'");
        }
    }

    private static File getTmpDir() throws IOException {
        String tmpDirName = System.getProperty("java.io.tmpdir");
        if (tmpDirName == null) {
            tmpDirName = "";
        } else {
            tmpDirName += File.separator;
        }
        Random r = new Random();
        String name = tmpDirName + "kilim$" + r.nextLong();
        File rootDir = new File(name);
        if (!rootDir.mkdir()) {
            throw new IOException("Unable to make tmp directory " + rootDir.getAbsolutePath());
        }
        return rootDir;
    }

    private static void deleteDir(File rootDir) {
        for (File f : rootDir.listFiles()) {
            if (f.isDirectory()) {
                deleteDir(f);
            } else {
                if (!f.delete()) {
                    System.err.println("Unable to delete " + f.getAbsolutePath());
                }
            }
        }
        if (!rootDir.delete()) {
            System.err.println("Unable to delete " + rootDir.getAbsolutePath());
        }
    }

    private static void addClasses(List<ClassInfo> ret, String pkgName, File dir)
        throws IOException {
        for (File f : dir.listFiles()) {
            String fname = f.getName();
            if (f.isDirectory()) {
                String qname = pkgName + fname + ".";
                addClasses(ret, qname, f);
            } else if (fname.endsWith(".class")) {
                String qname = pkgName + fname.substring(0, fname.length() - 6);
                ret.add(new ClassInfo(qname, readFile(f)));
            } else {
                System.err.println("Unexpected file : " + f.getAbsolutePath());
            }
        }
    }

    private static byte[] readFile(File f) throws IOException {
        int len = (int) f.length();
        byte[] buf = new byte[len];
        FileInputStream fis = new FileInputStream(f);
        int off = 0;
        while (len > 0) {
            int n = fis.read(buf, off, len);
            if (n == -1)
                throw new IOException("Unexpected EOF reading " + f.getAbsolutePath());
            off += n;
            len -= n;
        }
        return buf;
    }

    private static void writeFile(File f, byte[] srcCode) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(srcCode);
        fos.close();
    }

    private static class SourceInfo {
        public SourceInfo(String nm, String code) {
            className = nm;
            srcCode = code;
        }

        public String className;
        public String srcCode;
    }
}
