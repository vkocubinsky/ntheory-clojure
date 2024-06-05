(defproject ntheory "0.10.0"
  :description "Number Theory with Clojure"
  :url "https://github.com/vkocubinsky/ntheory-clojure"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.3"]]
  :repl-options {:init-ns user
                 :init
                 
                 (do
                   (require '[vk.ntheory.basic :as b]
                                '[vk.ntheory.primes :as p]
                                '[vk.ntheory.arithmetic-functions :as f]
                                '[vk.ntheory.congruences :as c]
                                '[vk.ntheory.primitive-roots :as r]
                                )
                   (set! *warn-on-reflection* true))})
