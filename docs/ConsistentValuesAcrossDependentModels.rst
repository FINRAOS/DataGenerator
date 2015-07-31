Producing Consistent Values Across Dependent Models
===================================================

An Example
----------

Suppose one had two models which modeled two separate but related aspects of a bank's accounting system. One model could produce withdraw and deposit transactions for bank accounts, and the other could produce the personal information tied to each bank account such as account owner name and address. The transactions model would be dependent on the account info model as the transactions model would need to know things like the account id number. As the relation between the models is many-to-one (each account can have multiple transactions) producing the information for accounts and transactions side by side in a single model could be rather difficult. In a many-to-many situation even more so.

A Possible Strategy
-------------------

In the model
~~~~~~~~~~~~

Instead of producing the values shared between the accounts and transactions models, both models can use placeholder macros. If both models have an 'accountNumber' variable both could use '#accountNumber' to indicate the value for the variable. The macro would be replaced with an actual account number at the end. 

Post Processing
~~~~~~~~~~~~~~~

To handle the missing account number value make a transformer that when given a unique seed representing an account will produce the needed information for that account::

    public class AccountTransformer implements DataTransformer {

        private int uniqueAccountSeed;

        public void setUniqueAccountSeed(int seed) {
            uniqueAccountSeed = seed;
        }

        public void transform(DataPipe dataPipe) {
            dataPipe.getDataMap().put("accountNumber", generateAccountNumber(uniqueAccountSeed));
        }

        public String generateAccountNumber(int seed) {
            Random random = new Random(seed);
            StringBuilder accountNumber = new StringBuilder();

            for (int i = 0; i < 10; i++)
                accountNumber.append(random.nextInt(10));

            return accountNumber.toString();
        } 

    }

If there are other model values tied to the same account, such as each account having an account type in addition to an account number, those are easy to add in as well::

    private int uniqueAccountSeed;

    ...

    public void transform(DataPipe dataPipe) {
        dataPipe.getDataMap.put("accountNumber", generateAccountNumber(uniqueAccountSeed));
        dataPipe.getDataMap.put("accountType", generateAccountType(uniqueAccountSeed));
    }

    ...

    public String generateAccountType(int seed) {
        Random random = new Random(seed);
        if (random.next() > 0.5) {
            return "Checking";
        } else {
            return "Savings";
        }
    }

To have one hundred accounts, one can vary the value of uniqueAccountSeed from 0 to 99. This transformer will give random values for information related to each account, but will produce those values in a repeatable way to make them consistent for runs of either the accounts or transactions model. The account info model would walk through each of the 100 accounts in order when writing out output, ::

    public class AccountInfoConsumer extends DataConsumer {

        private AccountTransformer accounts;
        private int currentAccount;

        public AccountInfoConsumer() {
            accounts = new AccountsTransformer();
            this.addDataTransformer(accounts);
            currentAccount = 0;
        }

        public int consume(Map<String, String> row) {
            if (currentAccount >= 100)
                return 0;

            accounts.setUniqueAccountSeed(currentAccount);
            currentAccount++;
            return super.consume(row);
        }
    }

and the transaction model would choose from the accounts randomly. ::

    public class TransactionConsumer extends DataConsumer {

        private AccountTransformer accounts;

        public TransactionConsumer() {
            accounts = new AccountsTransformer();
            this.addDataTransformer(accounts);
        }

        public int consume(Map<String, String> row) {
            accounts.setUniqueAccountSeed((int) (Math.random() * 100));
            return super.consume(row);
        }
    }

Now any transactions produced will use account numbers that have corresponding rows with the correct information in the accounts model output.
