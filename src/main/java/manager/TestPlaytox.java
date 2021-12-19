package manager;

import model.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestPlaytox {

    private static final int NUMBER_OF_ACCOUNT = 4;
    private static final int NUMBER_OF_THREADS = 2;
    private static final Logger LOGGER = LogManager.getLogger();

    private final List<Account> arrayAccounts = Collections.synchronizedList(new ArrayList<>());

    public void mainProcess() {
        LOGGER.info("Program is starting");
        for (int j = 0; j < NUMBER_OF_ACCOUNT; j++) {
            Account account = new Account(String.valueOf(UUID.randomUUID()));
            LOGGER.info("Created account with id = " + account.getId());
            arrayAccounts.add(account);
        }
        Thread[] threadsTransaction = new Thread[NUMBER_OF_THREADS];
        for (int z = 0; z < NUMBER_OF_THREADS; z++) {
            TransactionProcessor transactionProcessor = new TransactionProcessor(arrayAccounts);
            threadsTransaction[z] = new Thread(transactionProcessor);
            threadsTransaction[z].start();
        }
        try {
            for (Thread threads : threadsTransaction)
                threads.join();
        } catch (InterruptedException ignored) {}
        int sum = 0;
        for (int i = 0; i < NUMBER_OF_ACCOUNT; i++) {
            sum = sum + arrayAccounts.get(i).getMoney();
        }
        LOGGER.info("Program finished. Sum all accounts: " + sum);
    }
}
