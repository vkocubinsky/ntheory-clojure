(ns vk.ntheory.congruence
  (:require
   [vk.ntheory.basic :as b]))

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




