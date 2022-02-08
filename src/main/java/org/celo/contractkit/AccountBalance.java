package org.celo.contractkit;

import java.math.BigInteger;

public class AccountBalance {
    public final BigInteger CELO;
    public final BigInteger cUSD;
    public final BigInteger lockedCELO;
    public final BigInteger pending;
    public final BigInteger cEUR;
    public final BigInteger cBRL;

    public AccountBalance(
            BigInteger CELO,
            BigInteger cUSD,
            BigInteger lockedCELO,
            BigInteger pending,
            BigInteger cEUR,
            BigInteger cBRL
    ) {
        this.CELO = CELO;
        this.cUSD = cUSD;
        this.lockedCELO = lockedCELO;
        this.pending = pending;
        this.cEUR = cEUR;
        this.cBRL = cBRL;
    }

    static class Builder {
        private BigInteger celo;
        private BigInteger cUSD;
        private BigInteger lockedCELO;
        private BigInteger pending;
        private BigInteger cEUR;
        private BigInteger cBRL;

        public Builder setCELO(BigInteger celo) {
            this.celo = celo;
            return this;
        }

        public Builder setcUSD(BigInteger cUSD) {
            this.cUSD = cUSD;
            return this;
        }

        public Builder setLockedCELO(BigInteger lockedCELO) {
            this.lockedCELO = lockedCELO;
            return this;
        }

        public Builder setPending(BigInteger pending) {
            this.pending = pending;
            return this;
        }

        public Builder setcEUR(BigInteger cEUR) {
            this.cEUR = cEUR;
            return this;
        }

        public Builder setcBRL(BigInteger cBRL) {
            this.cBRL = cBRL;
            return this;
        }

        public AccountBalance createAccountBalance() {
            return new AccountBalance(celo, cUSD, lockedCELO, pending, cEUR, cBRL);
        }
    }
}
