Quick Start
===========

Here's the quickest route to getting "up and running" with DG.

Install Java and Maven installed on your system if you do not have them already. If you do not have an existing Maven project, you can create one within your favorite IDE, or by using the `Maven Quickstart Archetype <http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html>`_. If you have an existing Maven project, you can add the DG Core dependency to your POM using these coordinates::

	<dependency>
   		 <groupId>org.finra.datagenerator</groupId>
   		 <artifactId>dg-core</artifactId>
   		 <version>2.0</version>
	</dependency>

For this example, we will use the built-in `SCXMLDataModels <SCXMLDataModels.rst>`_ and `Multithreaded <Multithreaded.rst>`_. The following model defines three variables,
with two variables having multiple possible values, and one having a value of "customplaceholder", to be set by a custom transformer::

	<scxml xmlns="http://www.w3.org/2005/07/scxml"
		xmlns:cs="http://commons.apache.org/scxml"
		version="1.0"
		initial="start">
	
		<state id="start">
			<transition event="SETV1" target="SETV1"/>
		</state>
	
		<state id="SETV1">
			<onentry>
				<assign name="var_out_V1_1" expr="set:{A1,B1,C1}"/>
				<assign name="var_out_V1_2" expr="set:{A2,B2,C2}"/>
				<assign name="var_out_V1_3" expr="77"/>
			</onentry>
			<transition event="SETV2" target="SETV2"/>
		</state>
		
		<state id="SETV2">
			<onentry>
				<assign name="var_out_V2" expr="set:{1,2,3}"/>
				<assign name="var_out_V3" expr="#{customplaceholder}"/>
                		<dg:transform name="EQ"/>
                		<assign name="var_out_V4" expr="${var_out_V3}"/>
			</onentry>
			<transition event="end" target="end"/>
		</state>

		<state id="end">
			<!-- We're done -->
		</state>
	</scxml>

Now we need a transformer capable of interpreting the "customplaceholder" used in the assignment of V3. Let's say that "customplaceholder"
represents the need the insert a random number into the dataset. We can construct a custom DataTransformer to transform "customplaceholder"
into a random integer::

	public class SampleMachineTransformer implements DataTransformer {

	   	 private static final Logger log = Logger.getLogger(SampleMachineTransformer.class);
   		 private final Random rand = new Random(System.currentTimeMillis());

   		 /**
   		  * The transform method for this DataTransformer
   		  * @param cr a reference to DataPipe from which to read the current map
   		  */
   		 public void transform(DataPipe cr) {
   		     for (Map.Entry<String, String> entry : cr.getDataMap().entrySet()) {
   		         String value = entry.getValue();
		
   		         if (value.equals("#{customplaceholder}")) {
   		             // Generate a random number
   		             int ran = rand.nextInt();
   		             entry.setValue(String.valueOf(ran));
   		         }
   		     }
   		 }
	}

In order to assign V3's value to V4, V3's expression should be transformed before the assignment.
It can be achieved by `DG_Transform <tags/DG_Transform.rst>`_ tag.

Finally, we can create a driver for our data generation which configures an SCXMLEngine to use a DefaultDistributor and DataConsumer to solve
our problem::
	
   	 public static void main(String[] args) {
	
   		     Engine engine = new SCXMLEngine();
		
   		     //will default to samplemachine, but you could specify a different file if you choose to
   		     InputStream is = CmdLine.class.getResourceAsStream("/" + (args.length == 0 ? "samplemachine" : args[0]) + ".xml");
		
   		     engine.setModelByInputFileStream(is);
		
   		     // Usually, this should be more than the number of threads you intend to run
   		     engine.setBootstrapMin(1);
		
   		     //Prepare the consumer with the proper writer and transformer
   		     DataConsumer consumer = new DataConsumer();
   		     consumer.addDataTransformer(new SampleMachineTransformer());
   		     consumer.addDataWriter(new DefaultWriter(System.out,
   			             new String[]{"var_out_V1_1", "var_out_V1_2", "var_out_V1_3", "var_out_V2", "var_out_V3", "var_out_V4"}));
		
   		     //Prepare the distributor
   		     DefaultDistributor defaultDistributor = new DefaultDistributor();
   		     defaultDistributor.setThreadCount(1);
   		     defaultDistributor.setDataConsumer(consumer);
   		     Logger.getLogger("org.apache").setLevel(Level.WARN);
		
   		     engine.process(defaultDistributor);
	    }
