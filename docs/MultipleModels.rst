Executing Multiple Models Simultaneously in a Single Run
========================================================

Running Multiple Models
-----------------------

There are many strategies one could use if one's the data generation application required running multiple models at once. This example demonstrates just one approach.

SearchDistributor Capabilities
------------------------------

SearchDistributors expect a List of SearchFrontiers to distribute, but individual SearchFronties do not need to all be the same type, nor do the SearchFrontiers need to represent the same SCXML model. One way to execute multiple models at once is to pass to the DefaultDistributor (or other SearchDistributor of choice) an heterogeneous List of SearchFrontiers made up of SearchFrontiers for each of the multiple models.


Constructing an Heterogeneous List of Search Frontiers
------------------------------------------------------

Each call to process(SearchDistributor searchDistributor) for an Engine (such as the SCXML engine) produces a List of SearchFrontiers that are passed along to the given SearchDistributor. With a dummy search distributor ::

    public class DummyDistributor implements SearchDistributor {

        private List<Frontier> frontierList;

        public SearchDistributor setDataConsumer(DataConsumer dataConsumer) {
            return this;
        }

        public void distribute(List<Frontier> frontiers) {
            frontierList = frontiers;
        }

        public List<Frontier> getFrontierList() {
            return frontierList;
        }
    }

one can acquire the produced List of SearchFrontiers for further processing before passing them along to the actual SearchDistributor. Concatenate the results of different engine runs to produce an heterogeneous list of SearchFrontiers.