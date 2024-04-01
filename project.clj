(defproject ntheory "0.7.0"
  :description "Number Theory with Clojure"
  :url "https://github.com/vkocubinsky/ntheory-clojure"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.2"]]
  :repl-options {:init-ns user
                 :init (require '[vk.ntheory.basic :as b]
                                '[vk.ntheory.primes :as p]
                                '[vk.ntheory.arithmetic-functions :as af]
                                '[vk.ntheory.congruences :as c]
                                )})
