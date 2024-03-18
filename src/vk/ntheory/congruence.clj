(ns vk.ntheory.congruence
  (:require
   [vk.ntheory.basic :as b]
   [vk.ntheory.validation :as v]))

(defn- mod-m [m] #(mod % m))

(defn solve
  "Solve congruence f(x) ≡ 0 (mod m).
  Solve congruence by try each number of completely residue system.
  Returns residues satisfied to the congruence.
  Parameters:
  f - function accept one argument
  m - modulo "
  [f m]
  (->> (range m)
       (filter (comp zero? (mod-m m) f))))

(defn solve-linear
  "Solve congruence ax ≡ b (mod m). Returns sorted set."
  [a b m]
  (let [[d x0' _] (b/gcd-extended a m)]
    (if (b/divides? d b)
      (let [c (/ b d)
            x0 (* x0' c)
            m' (/ m d)]
        (->> (range d)
             (map #(+ x0 (* m' %)))
             (map (mod-m m))
             (apply sorted-set)))

      (sorted-set))))


(defn solve-2-remainders
  "Solve system of two congruences such that:
  x ≡ c₁ (mode m₁)
  x ≡ c₂ (mod m₂)"
  [m1 c1
   m2 c2]
  (v/check-integer-pos m1)
  (v/check-integer-pos m2)
  (let [[d a' _] (b/gcd-extended m1 m2)]
    (when (b/divides? d (- c2 c1))
      (let [c (/ (- c2 c1) d)
            a (* a' c)
            M (/ (* m1 m2) d)]
        [M (mod (+ c1 (* m1 a)) M)]
        )
      )
    )
)


(defn solve-n-reminders
  "Solve system of n congruences such that
  x ≡ c₁ (mod m₁)
  x ≡ c₂ (mod m₂)
  ...
  Parameter `m` is a map of modulus to remainder i.e. {m₁ c₁ m₂ c₂ ... }.
  Returns pair [M r], where M is least common mulitple of m₁, m₂, ..., and
  r is residue to modulus M."
  [m]
  (condp = (count m)
    1 (let [[x & _] m] x)
    2 (let [[[m1 c1] [m2 c2] & _] m] (solve-2-remainders m1 c1 m2 c2) )
    "more than 2")
  )


(solve-n-reminders {34 9})
(solve-n-reminders {34 9 19 4})
