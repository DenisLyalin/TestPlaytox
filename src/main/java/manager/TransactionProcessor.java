package manager;

import model.Account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionProcessor implements Runnable {

    private static final int NUMBER_OF_TRANSACTIONS = 30;
    private static final int TRANSFER_RANGE = 10000;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger COUNT_TRANSACTION = new AtomicInteger(1);

    private final List<Account> arrayAccounts;

    public TransactionProcessor(final List<Account> arrayAccounts) {
        this.arrayAccounts = arrayAccounts;
    }

    public void run() {
        int transactionNumber = COUNT_TRANSACTION.getAndAdd(1);
        int numberOfAccounts = arrayAccounts.size();
        Random random = new Random();
        while (transactionNumber <= NUMBER_OF_TRANSACTIONS) {
            int buyerIndex = random.nextInt(numberOfAccounts);
            int sellerIndex = random.nextInt(numberOfAccounts);
            while (buyerIndex == sellerIndex) {
                sellerIndex = random.nextInt(numberOfAccounts);
            }
            int moneyTransfer = random.nextInt(TRANSFER_RANGE);
            final Account buyer = arrayAccounts.get(buyerIndex);
            final Account seller = arrayAccounts.get(sellerIndex);
            if (buyerIndex < sellerIndex) {
                synchronized (buyer) {
                    synchronized (seller) {
                        doTransfer(buyer, seller, moneyTransfer, transactionNumber);
                    }
                }
            } else {
                synchronized (seller) {
                    synchronized (buyer) {
                        doTransfer(buyer, seller, moneyTransfer, transactionNumber);
                    }
                }
            }
            try {
                int pause = 1000 + random.nextInt(1000);
                Thread.sleep(pause);
            } catch (InterruptedException ignored) {
            }
            transactionNumber = COUNT_TRANSACTION.getAndAdd(1);
        }
    }

    private void doTransfer(final Account buyer, final Account seller,
                            final int moneyTransfer, final int transactionNumber) {
        if (moneyTransfer <= buyer.getMoney()) {
            buyer.setMoney(buyer.getMoney() - moneyTransfer);
            seller.setMoney(seller.getMoney() + moneyTransfer);
            LOGGER.info("Transaction #" + transactionNumber + ": transfer amount - " + moneyTransfer +
                    ". The balance of buyer (id: " + buyer.getId() + ") has become: " + buyer.getMoney() +
                    ". The balance of seller (id: " + seller.getId() + ") has become: " + seller.getMoney() + ";");
        } else {
            LOGGER.error("Transaction #" + transactionNumber + " - failed. Transfer amount - " +
                    moneyTransfer + " is more than the balance of buyer (id: " + buyer.getId() + "): " + buyer.getMoney() + ";");
        }
    }
}
