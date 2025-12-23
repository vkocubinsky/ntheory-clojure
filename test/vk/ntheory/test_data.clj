(ns vk.ntheory.test-data)


(def factorization-data-upper-limit 31)

(def factorization-data
  "Test data.

  Columns:
  - number: number for test
  - factors: factors with their multiplicity
  - distinct-factors: discitnct factors
  - prime?: is the number is a prime
  - primes: primes less or equal number
  
  "
  [{:number 1
    :factors []
    :factor-counts []
    :distinct-factors []
    :prime? false
    :primes []}
   {:number 2
    :factors [2]
    :factor-counts [[2 1]]
    :distinct-factors [2]
    :prime? true
    :primes [2]}
   {:number 3
    :factors [3]
    :factor-counts [[3 1]]
    :distinct-factors [3]
    :prime? true
    :primes [2 3]}
   {:number 4
    :factors [2 2]
    :factor-counts [[2 2]]
    :distinct-factors [2]
    :prime? false
    :primes [2 3]}
   {:number 5
    :factors [5]
    :factor-counts [[5 1]]
    :distinct-factors [5]
    :prime? true
    :primes [2 3 5]}
   {:number 6
    :factors [2 3]
    :factor-counts [[2 1] [3 1]]
    :distinct-factors [2 3]
    :prime? false
    :primes [2 3 5]}
   {:number 7
    :factors [7]
    :factor-counts [[7 1]]
    :distinct-factors [7]
    :prime? true
    :primes [2 3 5 7]}
   {:number 8
    :factors [2 2 2]
    :factor-counts [[2 3]]
    :distinct-factors [2]
    :prime? false
    :primes [2 3 5 7]}
   {:number 9
    :factors [3 3]
    :factor-counts [[3 2]]
    :distinct-factors [3]
    :prime? false
    :primes [2 3 5 7]}
   {:number 10
    :factors [2 5]
    :factor-counts [[2 1] [5 1]]
    :distinct-factors [2 5]
    :prime? false
    :primes [2 3 5 7]}
   {:number 11
    :factors [11]
    :factor-counts [[11 1]]
    :distinct-factors [11]
    :prime? true
    :primes [2 3 5 7 11]}
   {:number 12
    :factors [2 2 3]
    :factor-counts [[2 2] [3 1]]
    :distinct-factors [2 3]
    :prime? false
    :primes [2 3 5 7 11]}
   {:number 13
    :factors [13]
    :factor-counts [[13 1]]
    :distinct-factors [13]
    :prime? true
    :primes [2 3 5 7 11 13]}
   {:number 14
    :factors [2 7]
    :factor-counts [[2 1] [7 1]]
    :distinct-factors [2 7]
    :prime? false
    :primes [2 3 5 7 11 13]}
   {:number 15
    :factors [3 5]
    :factor-counts [[3 1] [5 1]]
    :distinct-factors [3 5]
    :prime? false
    :primes [2 3 5 7 11 13]}
   {:number 16
    :factors [2 2 2 2]
    :factor-counts [[2 4]]
    :distinct-factors [2]
    :prime? false
    :primes [2 3 5 7 11 13]}
   {:number 17
    :factors [17]
    :factor-counts [[17 1]]
    :distinct-factors [17]
    :prime? true
    :primes [2 3 5 7 11 13 17]}
   {:number 18
    :factors [2 3 3]
    :factor-counts [[2 1] [3 2]]
    :distinct-factors [2 3]
    :prime? false
    :primes [2 3 5 7 11 13 17]}
   {:number 19
    :factors [19]
    :factor-counts [[19 1]]
    :distinct-factors [19]
    :prime? true
    :primes [2 3 5 7 11 13 17 19]}
   {:number 20
    :factors [2 2 5]
    :factor-counts [[2 2] [5 1]]
    :distinct-factors [2 5]
    :prime? false
    :primes [2 3 5 7 11 13 17 19]}
   {:number 21
    :factors [3 7]
    :factor-counts [[3 1] [7 1]]
    :distinct-factors [3 7]
    :prime? false
    :primes [2 3 5 7 11 13 17 19]}
   {:number 22
    :factors [2 11]
    :factor-counts [[2 1] [11 1]]
    :distinct-factors [2 11]
    :prime? false
    :primes [2 3 5 7 11 13 17 19]}
   {:number 23
    :factors [23]
    :factor-counts [[23 1]]
    :distinct-factors [23]
    :prime? true
    :primes [2 3 5 7 11 13 17 19 23]}
   {:number 24
    :factors [2 2 2 3]
    :factor-counts [[2 3] [3 1]]
    :distinct-factors [2 3]
    :prime? false
    :primes [2 3 5 7 11 13 17 19 23]}
   {:number 25
    :factors [5 5]
    :factor-counts [[5 2]]
    :distinct-factors [5]
    :prime? false
    :primes [2 3 5 7 11 13 17 19 23]}
   {:number 26
    :factors [2 13]
    :factor-counts [[2 1] [13 1]]
    :distinct-factors [2 13]
    :prime? false
    :primes [2 3 5 7 11 13 17 19 23]}
   {:number 27
    :factors [3 3 3]
    :factor-counts [[3 3]]
    :distinct-factors [3]
    :prime? false
    :primes [2 3 5 7 11 13 17 19 23]}
   {:number 28
    :factors [2 2 7]
    :factor-counts [[2 2] [7 1]]
    :distinct-factors [2 7]
    :prime? false
    :primes [2 3 5 7 11 13 17 19 23]}
   {:number 29
    :factors [29]
    :factor-counts [[29 1]]
    :distinct-factors [29]
    :prime? true
    :primes [2 3 5 7 11 13 17 19 23 29]}
   {:number 30
    :factors [2 3 5]
    :factor-counts [[2 1] [3 1] [5 1]]
    :distinct-factors [2 3 5]
    :prime? false
    :primes [2 3 5 7 11 13 17 19 23 29]}
   {:number 31
    :factors [31]
    :factor-counts [[31 1]]
    :distinct-factors [31]
    :prime? true
    :primes [2 3 5 7 11 13 17 19 23 29 31]}])
