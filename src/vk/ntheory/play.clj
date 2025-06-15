(ns vk.ntheory.play)



(defn calc1 []
  (for [val (range 1 1000000)
    :let [k (Long/numberOfTrailingZeros val)
          v (bit-shift-right val k)]]
      v
      
    )
  )


(defn calc2 []
  (for [val (range 1 1000000)
    :let [k 2
          v (quot val k)]]
      v
      
    )
  )


(time (dorun (calc1)))

(time (dorun (calc2)))


