(ns vk.ntheory.congruence
  (:require
     [vk.ntheory.basic :as b]
     [clojure.math :as math]))


(defn mod-m [m] #(mod % m))


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
  (let [[d x0' y0'] (b/gcd-extended a m)]
    (if (b/divides? d b)
      (let [c (/ b d)
            x0 (* x0' c)
            m' (/ m d)]
        (->> (range d)
             (map #(+ x0 (* m' %)))
             (map (mod-m m))
             (apply sorted-set)
             )
        )
      (sorted-set)
      ))
  )

(defn solve-two-linear
  "Solve system of congruences a₁x ≡ b₁ (mod m), a₂x ≡ b₂ (mod m)."
  [[a1 b1] [a2 b2] m])


