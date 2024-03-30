(ns vk.ntheory.primitive-roots
  "Primitive roots."
  (:require [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            [vk.ntheory.arithmetic-functions :as af]))

(defn order'
  "Brute force version of find multiplicative order."
  [a m]
  (b/check-int-non-neg a)
  (b/check-int-pos m)
  (b/check-relatively-prime a m)
  (loop [k 1
         an (mod a m)]
    (if (= 1 an)
      k
      (recur (inc k) (b/m* m an a)))))

(defn order
  "Find multiplicative order of given integer `a`."
  [a m]
  (b/check-int-non-neg a)
  (b/check-int-pos m)
  (b/check-relatively-prime a m)
  (->> m
       af/totient
       af/divisors
       sort
       (filter #(= 1 (b/m** m a %)))
       first))

(defn classify-residues
  [m]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count m)]
    (cond
      (= m 1) :1
      (and (nil? p2) (= a1 1)) :prime ;; p
      (and (nil? p2) (> a1 1)) :prime-power ;; p^a
      )))

(defmulti reduced-residues classify-residues :default :composite)

(defmethod reduced-residues :1
  [m]
  '(1))

(defmethod reduced-residues :prime
  [m]
  (range 1 m))

(defmethod reduced-residues :prime-power
  [m]
  (let [[[p a]] (p/int->factors-count m)]
    (->> (range 1 m)
         (remove #(b/divides? p %)))))

;; For now brute force implementation
(defmethod reduced-residues :composite
  [m]
  (->> (range 1 m)
       (filter #(= 1 (b/gcd m %)))))

(defn primitive-root'?
  "Brute force version of primitive-root?"
  [a m]
  (= (order a m) (af/totient m)))

(defn primitive-root?
  [a m]
  (b/check-relatively-prime a m)
  (let [phi-p (af/totient m)]
    (->> phi-p
         (p/int->factors-distinct)
         (map #(/ phi-p %))
         (map #(b/m** m a %))
         (every? #(not (= 1 %))))))

(defn classify-modulo
  [n]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count n)]
    (cond
      (= n 1) :1
      (= n 2) :2
      (= n 4) :4
      (and (nil? p2) (= a1 1)) :odd-prime ;; p
      (and (nil? p2) (> a1 1)) :odd-prime-power ;; p^a
      (and (= p1 2) (= a1 1) (not (nil? p2)) (nil? p3)) :2-odd-prime-power)))

(defn primitive-roots'
  "Brute force version of search primitive roots."
  [m]
  (b/check-int-pos m)
  (->> (reduced-residues m)
       (filter #(primitive-root? % m))))

(defmulti find-primitive-root classify-modulo)

(defmethod find-primitive-root :1
  [m]
  1)

(defmethod find-primitive-root :2
  [m]
  1)

(defmethod find-primitive-root :4
  [m]
  3)

(defmethod find-primitive-root :odd-prime
  [m]
  (->> (range 1 m)
       (filter #(primitive-root? % m))
       first))

(defmethod find-primitive-root :odd-prime-power
  [m]
  (let [[[p a]] (p/int->factors-count m)
        g (find-primitive-root p)
        c (b/m** (* p p) g (dec p))]
    (if (= 1 c)
      (+ g p)
      g)))

(defmethod find-primitive-root :2-odd-prime-power
  [m]
  (let [[[_ _] [p a]] (p/int->factors-count m)
        g (find-primitive-root (b/pow p a))]
    (if (odd? g) g
        (+ g (b/pow p a)))))

(defmethod find-primitive-root :default
  [m]
  nil)

;; 997, 9973

(defn combinations'
  "Generates combinations of sequences.
  Parameters:
    start - start nubmer common for all sequences from second argument.
    xss - sequence of sequences, for instance
  (combination 0 [(range 10) (range 10)]) generates all pairs [i,j]
  where 0 <= i < 10 , 0<= j < 10. All sequences must start from the same
  element which is the first parameter. 
     "
  ([start xss]
   (lazy-seq
    (cons (map first xss)
          (combinations start (mapv cycle xss) (dec (count xss))))))

  ([start css k]
   (when-not (neg? k)
     (let [cs (get css k) ;; get sequence
           cs (rest cs)   ;; shift sequence
           e (first cs)   ;; to detect overflow
           css (assoc css k cs) ;; assoc shifted sequence 
           ]
       (if (= e start)
         (lazy-seq (combinations start css (dec k))) ;; overflow
         (lazy-seq (cons
                    (map first css)
                    (combinations start css (dec (count css))))))))))


(defn combinations
  ([xss] (combinations (mapv first xss) (mapv cycle xss)))
  ([starts css]
   (lazy-seq
    (println "out " (map first css))
    (cons (map first css)
          (loop [css css
                 k (dec (count css))]
            (when-not (neg? k)
              (let [start (get starts k)
                    cs (rest (get css k))
                    e (first cs)
                    css (assoc css k cs)]
                (if (= e start)
                  (do
                    (println "e = " e " start = " start )
                    (recur css (dec k)))
                  (combinations-cycle starts css)))))))))

(combinations [(range 4) (range 5)])

