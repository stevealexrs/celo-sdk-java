package org.celo.contractkit;

import static org.celo.contractkit.TestData.PRIVATE_KEY_1;
import static org.celo.contractkit.TestData.PUBLIC_KEY_1;
import static org.celo.contractkit.TestData.PUBLIC_KEY_2;
import static org.celo.contractkit.TestUtils.ONE_GWEI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.celo.contractkit.wrapper.GoldTokenWrapper;
import org.celo.contractkit.wrapper.StableTokenBRLWrapper;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
// TODO: Test once it is launch, should work since it is a copy and paste from StableTokenEUR
public class StableTokenBRLTest {

    ContractKit contractKit;
    StableTokenBRLWrapper stableTokenBRL;

    @Before
    public void initialize() {
        Web3j web3j = Web3j.build(new HttpService(ContractKit.ALFAJORES_TESTNET));
        contractKit = new ContractKit(web3j);
        contractKit.addAccount(PRIVATE_KEY_1);
        stableTokenBRL = contractKit.contracts.getStableTokenBRL();
    }

    @Test
    public void testGetContractAddress() throws Exception {
        assertEquals(BigInteger.valueOf(18), stableTokenBRL.decimals().send());
        // TODO: Change to actual celo brazilian currency name once it is launched
        assertEquals("Celo Brazilian Real", stableTokenBRL.name().send());
        assertEquals("cBRL", stableTokenBRL.symbol().send());
        assertNotNull(stableTokenBRL.getInflationParameters().send());
        assertEquals("0xaa963fc97281d9632d96700ab62a4d1340f9a28a", stableTokenBRL.owner().send());
        TestUtils.assertIsPositive(stableTokenBRL.totalSupply().send());
        TestUtils.assertIsPositive(stableTokenBRL.balanceOf(PUBLIC_KEY_1).send());
    }

    @Test
    public void testTransfer() throws Exception {
        BigInteger initialBalance = stableTokenBRL.balanceOf(PUBLIC_KEY_2).send();

        TransactionReceipt tx = stableTokenBRL.transfer(PUBLIC_KEY_2, ONE_GWEI).send();
        assertTrue(tx.isStatusOK());
        assertNotNull(tx.getTransactionHash());

        BigInteger finalBalance = stableTokenBRL.balanceOf(PUBLIC_KEY_2).send();
        assertEquals(initialBalance.add(ONE_GWEI), finalBalance);
    }

    @Test
    public void testTransferFrom() throws Exception {
        BigInteger initialBalance = stableTokenBRL.balanceOf(PUBLIC_KEY_2).send();

        TransactionReceipt increaseTxHash = stableTokenBRL.increaseAllowance(contractKit.getAddress(), ONE_GWEI).send();
        assertTrue(increaseTxHash.isStatusOK());
        assertNotNull(increaseTxHash.getTransactionHash());

        TransactionReceipt tx = stableTokenBRL.transferFrom(PUBLIC_KEY_1, PUBLIC_KEY_2, ONE_GWEI).send();
        assertTrue(tx.isStatusOK());
        assertNotNull(tx.getTransactionHash());

        BigInteger finalBalance = stableTokenBRL.balanceOf(PUBLIC_KEY_2).send();
        assertEquals(finalBalance, initialBalance.add(ONE_GWEI));
    }

    @Test
    public void testTransferWithGoldFee() throws Exception {
        ContractKitOptions config = new ContractKitOptions.Builder()
                .setFeeCurrency(CeloContract.GoldToken)
                .build();

        Web3j web3j = Web3j.build(new HttpService(ContractKit.ALFAJORES_TESTNET));
        ContractKit contractKit = new ContractKit(web3j, config);
        contractKit.addAccount(PRIVATE_KEY_1);

        StableTokenBRLWrapper stableTokenBRL = contractKit.contracts.getStableTokenBRL();
        GoldTokenWrapper goldToken = contractKit.contracts.getGoldToken();

        // Get initial balances
        BigInteger initialBalance = stableTokenBRL.balanceOf(PUBLIC_KEY_1).send();
        BigInteger initialGoldBalance = goldToken.balanceOf(PUBLIC_KEY_1);

        // Transfer one gwei
        TransactionReceipt tx = stableTokenBRL.transfer(PUBLIC_KEY_2, ONE_GWEI).send();
        assertTrue(tx.isStatusOK());
        assertNotNull(tx.getTransactionHash());

        // Check final balance after transfer
        BigInteger finalBalance = stableTokenBRL.balanceOf(PUBLIC_KEY_1).send();
        assertEquals("Only specified amount without fee", initialBalance.subtract(ONE_GWEI), finalBalance);

        // Should
        BigInteger finalGoldBalance = goldToken.balanceOf(PUBLIC_KEY_1);
        assertEquals("Used gold fee", -1, finalGoldBalance.compareTo(initialGoldBalance));
    }
}
