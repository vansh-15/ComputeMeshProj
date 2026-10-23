package com.computemesh.worker_agent.service;

import org.springframework.stereotype.Service;

@Service
public class PrimeCountService {
    public long countPrimes(int limit) {
        if (limit < 2) {
            return 0;
        }

        long count = 0;

        for (int number = 2; number <= limit; number++) {
            if (isPrime(number)) {
                count++;
            }
        }

        return count;
    }

    private boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }

        for (int divisor = 2;
             divisor <= number / divisor;
             divisor++) {

            if (number % divisor == 0) {
                return false;
            }
        }

        return true;
    }
}
