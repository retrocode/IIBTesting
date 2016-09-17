package com.retrocode.iib.testing

import groovy.xml.XmlUtil
import junit.framework.Assert
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.diff.Diff
import org.xmlunit.util.Nodes
import org.xmlunit.util.Predicate
import spock.lang.Unroll

/**
 * Transaction integration test which uses the Spock framework (https://code.google.com/p/spock/) to assert XML output of IIB flow matched predefined expected value
 * assumes IIB component has been created which does MQ-Reply if JMS custom header TestingHeader=True is set
 * @author wrhett
 *
 */
class TestXMLToXMLSpecification extends BaseSpecification{

	// used by each test
	def scenarioDesc
	def scenarioNum
	def iibInputXMLFile
	def iibInputXMLFileFull
	def iibOutputFile
	def iibOutputFileFull
    def response
    String inputQueue = "TEST_INPUT"

	@Unroll("Testing scenario #scenarioNum - #scenarioDesc - XMM output matches expected file #iibInputXMLFile")
	def "Read XML from IIB response and assert the data is valid"(){

		setup:

        // input and expected result files are within this project
		iibInputXMLFileFull = "/src/test/resources/data/transaction/iib_input/${iibInputXMLFile}"
		iibOutputFileFull = "./src/test/resources/data/transaction/iib_output/${iibInputXMLFile}"

		when: "A message is put on the IIB input queue"

			// read the iib input file
			String inputMessage = XmlUtil.serialize(new XmlSlurper().parse(iibInputXMLFileFull))
			println("Putting a message on the queue")
            // send the message to IIB with the test header set and a temporary reply queue created so IIB can response back
            response = producer.requestBodyAndHeader("jms:$inputQueue?deliveryPersistent=false&exchangePattern=InOut&requestTimeout=90000&asyncConsumer=true&asyncStartListener=true&replyToType=Shared",inputMessage, 'TestingHeader', 'True', String.class)

		then: "Message on reply queue matches expected XML results"

            // parse the string response
            def outputXML = new XmlParser().parseText(response)
            println("Raw XML response output ${response}")

            // parse the expected output contained in the file
			def expectedStr = new File(iibOutputFileFull).text
			println("Raw Expected XML output ${expectedStr}")

            def outputXMLStr = XmlUtil.serialize(outputXML);


        // ignore all these elements which have dynamically generated keys and we don't want to use in the comparison
        def elementsToIgnore = ["TRN_ID","TRN_LN_ID","LN_ID","LN_SLRT_ID","LN_SLRT_TAX_ID","TRN_TOTAL_ID","LN_TAX_ID","LN_TENDER_ID"]

        // construct a XMLUnit2 DiffBuilder which ignores the aforementioned elements
		final Diff documentDiff = DiffBuilder
				.compare(expectedStr)
				.withTest(outputXMLStr)
                .withNodeFilter(new Predicate<Node>() {
                    @Override
                    public boolean test(Node n) {

                        boolean result = !(n instanceof Element && elementsToIgnore.contains(Nodes.getQName(n).getLocalPart()));
                        return result;
                    }
                })
                .ignoreWhitespace()
				.build();

            // compare the 2 XML strings
            def result = documentDiff.getDifferences()
            Assert.assertFalse(documentDiff.toString(), documentDiff.hasDifferences());

		where:
			// Spock data table, each row becomes a new test
			scenarioNum | scenarioDesc							|    iibInputXMLFile
			
			"1"			| "Simple sale with Visa Tender"		|	'POE-27-1.xml'

	}

}
