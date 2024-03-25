
# Table of Contents

1.  [About](#orgb91b6d5)
2.  [Notation](#org8c696dd)
3.  [Namespace `vk.ntheory.basic`](#org74d08e1)
    1.  [Check functions](#org8881899)
    2.  [Some predicates](#org3299f3a)
    3.  [Operations in $\mathbf{Z}/m\mathboldf{Z}$](#org58b2661)
    4.  [Power function](#orgb0135b1)
    5.  [Order function](#org64ccec3)
    6.  [Sign function](#org09a66a5)
    7.  [The greatest common divisor](#org5202fc8)
4.  [Namespace `vk.ntheory.primes`](#orga393653)
    1.  [Performance and cache](#org5bb1166)
5.  [Primes](#org4c87174)
6.  [Integer factorization](#orgbd49098)
7.  [Divisors](#org3aefcd4)
8.  [Arithmetical functions](#org3751181)
    1.  [Function equality](#org66bae74)
    2.  [Additive functions](#orgacba7b5)
    3.  [Multiplicative functions](#orgb2509ad)
    4.  [Higher order function for define multiplicative and additive functions](#orga96c70b)
    5.  [Some additive functions](#org6373501)
        1.  [Count of distinct primes - $\omega$](#org8e3a5a5)
        2.  [Total count of primes - $\Omega$](#orgf97b80c)
    6.  [Some multiplicative functions](#orga9de90f)
        1.  [Mobius function - $\mu$.](#orgb9dc070)
        2.  [Euler totient function - $\phi$](#org26ce038)
        3.  [Unit function - $\epsilon$](#orgf5a17d7)
        4.  [Constant one function - $1$](#org49a33a6)
        5.  [Divisors count - $\sigma_0$](#org0f1474e)
        6.  [Divisors sum - $\sigma_1$](#org3d9dde0)
        7.  [Divisors square sum](#org1d1a010)
        8.  [Divisors higher order function - $\sigma_{x}$](#org457286f)
        9.  [Liouville - $\lambda$](#org1b0873b)
    7.  [Some other arithmetic functions](#org5d7d34f)
        1.  [Mangoldt - $\Lambda$](#org3c75d8a)
        2.  [Chebyshev functions $\theta$ and $\psi$](#org374c279)
    8.  [Dirichlet convolution](#org0bcda65)



<a id="orgb91b6d5"></a>

# About

This project cover some topics in number theory such as integer
factorization, arithmetic functions, congruences, primitive roots.
Here defined set of well known arithmetic functions and one can define
custom arithmetic function. One can solve linear congruence or system
of linear congruences including case when moduli relatively prime.

I wrote this document `readme.org` with Emacs Org Mode. Then I
generate markdown file `readme.md` with Org Mode export to markdown
`C-c C-e m m`, and generate pdf file `readme.odf` with Org Mode export
to pdf `C-c C-e l p`.  Github by default show `readme.md` if a project
has such file.  It looks enough good, even math equation is
supported. But I see some issue with greek characters in table of
content and links. If it is a problem `readme.pdf` looks better. I use
Emacs babel for clojure to produce real output inside the document. 

In this document I load number theory packages as: 

    (require '[vk.ntheory.basic :as b])
    (require '[vk.ntheory.primes :as p])
    (require '[vk.ntheory.ar-func :as af])
    (require '[vk.ntheory.congruence :as c])
    (require '[vk.ntheory.primitive-roots :as pr])
    (require '[clojure.math :as math])

So below I will use above aliases.


<a id="org8c696dd"></a>

# Notation

-   $\mathbf N$ - Natural numbers, positive integers $1,2,3,\dots$
-   $\mathbf C$ - Complex numbers
-   $\mathbf Z$ - Integers $\dots -3, -2, -1, 0, 1, 2, 3, \dots$
-   $\mathbf Z/m\mathbf Z$ - Ring of integers modulo $m$


<a id="org74d08e1"></a>

# Namespace `vk.ntheory.basic`

Namespace `vk.ntheory.basic` contains some common functions, which
can be used directly or by other namespaces.

    (require '[vk.ntheory.basic :as b])


<a id="org8881899"></a>

## Check functions

There are set of `check-*` functions which can be helpful to validate
user input:

-   `check-int`
-   `check-int-pos`
-   `check-int-non-neg`
-   `check-int-non-zero`

All of above accept one argument, check does argument satisfy to
expectation, if does return argument, otherwise throw an exception.

There are also two helper function `check` and `check-not` which helps
to implement another `check-*` function for a predicate. 


<a id="org3299f3a"></a>

## Some predicates

Function `divides?` determine does one number divides another.

    (b/divides? 2 8)

    true


<a id="org58b2661"></a>

## Operations in $\mathbf{Z}/m\mathboldf{Z}$

Similar to addition function `+` and multiplication function `*` there
defined addition modulo m `m+` and multiplication modulo m `m*`. First
argument of these functions is a modulo.

For instance $2 + 4 \equiv 1 \pmod{5}$ in $\mathbf{Z}/m\mathbf{Z}$

    (b/m+ 5 2 4)

    1

and $2 \cdot 4 \equiv 3 \pmod 5$ in $\mathbf{Z}/m\mathbf{Z}$

    (b/m* 5 2 4)

    3

The fact that a modulo is a first argument allow bind modulo in let
expression and then use addition and multiplication modulo m without
specify a modulo.

    (let [m5* (partial b/m* 5)
          m5+ (partial b/m+ 5)]
      ;; ...
      (m5* 2 4))

    3

There is another helpful function modulo m - exponentiation. It is a
fast binary exponentiation algorithm described in D.Knuth, The Art of
Computer Programming, Volume II.

For instance, $101^{900} \equiv 701 \pmod{997}$

    (b/m** 997 101 900)

    701


<a id="orgb0135b1"></a>

## Power function

Clojure has built-in `clojure.math/pow` function, but it return
`java.lang.Double`. The library provide integer analog

    (b/pow 2 3)

    8


<a id="org64ccec3"></a>

## Order function

Order function $ord_p(n)$ is a greatest power of $p$ divides $n$

    (b/order 2 24)

    3


<a id="org09a66a5"></a>

## Sign function

    (mapv b/sign [(- 5) 10 0])

    [-1 1 0]


<a id="org5202fc8"></a>

## The greatest common divisor

The greatest common divisor of two integer $a$ and $b$ is an positive
integer $d$ which divide $a$ and $b$ and any other common divisor $a$
and $b$ divides $d$.

    (b/gcd 12 18)

    6

For convenience `(gcd 0 0)` is `0`.

Furthermore, if for any two integers $a$ and $b$ exists integers `s`
and $t$ such that $a s + b t = d$ , where d is the greatest common
divisor. For example, $6 = 12 (-1) + 18 (1)$

    (b/gcd-extended 12 18)

    [6 -1 1]


<a id="orga393653"></a>

# Namespace `vk.ntheory.primes`


<a id="org5bb1166"></a>

## Performance and cache

This library is designed to work with realtive small integers. Library
keep in cache least prime divisor table for fast integer
factorization.  Cache grows automatically. The strategy of growing is
extends cache to the least power of `10` more than required
number. For instance, if client asked to factorize number `18`, cache
grows to `100`, if client asked to factorize number `343`, cache grows
to `1000`. List of primes also cached and recalculated together
with least prime divisor table. Recalculation is not incremental, but
every recalculation of least prime divisor table make a table which is
in `10` times more than previous, and time for previous calculation is
`10` times less than for new one. So we can say that recalculation
spent almost all time for recalculate latest least prime divisor
table.

Internally, least prime divisor table is java array of int, so to store
least divisor table for first `1 000 000` number approximately `4M`
memory is required, `4` bytes per number.

Cache can be reset:

    (p/cache-reset!)

    {:least-divisor-table , :primes , :upper 0}

Least prime divisor table is implementation details, but one can see
it:

    ;; load first 10 numbers into cache
    (p/int->factors-map 5)

    {5 1}

For instance, for get least prime divisor of number 6 we need to get
element with index 6, which is 2. Index zero is not used, value for
index 1 is 1.


<a id="org4c87174"></a>

# Primes

`primes` function returns prime numbers which not exceeds given `n`.

    (p/primes 30)

    (2 3 5 7 11 13 17 19 23 29)


<a id="orgbd49098"></a>

# Integer factorization

Every integer more than $1$ can be represented uniquely as a product
of primes.

$$
n = {p_1}^{a_1} {p_2}^{a_2} \dots {p_k}^{a_k}
$$

or we can write it in more compact form:

$$
n = \prod_{i=1}^{k} {p_i}^{a_i}
$$

or even write as:

$$n = \prod_{p|n} p^a$$

If we accept that empty product is $1$ we can say that every natural
number can be represent uniquely as a product of primes. For example
$360 = 2^3 3^2 5^1$.

There are some functions to factorize integers. Each of them accept
natural number as an argument and returns factorized value. It have
slightly different output, which may be more appropriate to different
use cases. For each factorize function there is also inverse function,
which accept factorized value and convert it back to integer.

1-st factorization representation is ordered sequence of primes:

    (p/int->factors 360)

    (2 2 2 3 3 5)

    (p/factors->int [2 2 2 3 3 5])

    360

2-nd factorization representation is ordered sequence of primes
splited by partitions by a prime:

    (p/int->factors-partitions 360)

    ((2 2 2) (3 3) (5))

    (p/factors-partitions->int [[2 2 2] [3 3] [5]])

    360

3-rd factorization representation is ordered sequence of pairs `[p
k]`, where `p` is a prime and `k` is a power of prime

    (p/int->factors-count 360)

    ([2 3] [3 2] [5 1])

    (p/factors-count->int [[2 3] [3 2] [5 1]])

    360

4-th factorization representation is very similar to 3-rd, but it
is a map. And it has the same inverse function as 3-rd.

    (p/int->factors-map 360)

    {2 3, 3 2, 5 1}

    (p/factors-count->int {2 3, 3 2, 5 1})

    360

Implementation of factorization use least prime divisor table. To
factorize number `n` it is enough to calculate least divisor table
with size less or equals to $\sqrt n$. 


<a id="org3aefcd4"></a>

# Divisors

For get list of all divisors of number `n` there is `divisor`
function. List of divisors is unordered.

    (f/divisors 30)

    class clojure.lang.Compiler$CompilerException


<a id="org3751181"></a>

# Arithmetical functions

Arithmetical function is an any function which accept natural number
and return complex number $f: \mathbf N \to \mathbf C$. The library mostly works
with functions which also returns integer $f: \mathbf N \to \mathbf Z$.


<a id="org66bae74"></a>

## Function equality

Two arithmetical function $f$ and $g$ are equal if $f(n)=g(n)$ for all
natual $n$. There is helper function `f-equlas` which compare two
functions on some sequence of natual numbers. Function `f=`
accept two functions and optionally sequence of natural numbers. There
is a default for sequence of natural numbers, it is a variable
`default-natural-sample`, which is currently `range(1,100)`.

If we like identify does two function `f` and `g` equals on some
sequence of natural number we can for example do next:

    ;; Let we have some f and g
    (def f identity)
    (def g (constantly 1))
    ;; Then we able to check does those functions are equals
    (f/f= f g)
    (f/f= f g (range 1 1000))
    (f/f= f g (filter even? (range 1 100)))


<a id="orgacba7b5"></a>

## Additive functions

Additive function is a function for which

$$ f(mn) = f(m) + f(n)$$

if $m$ relatively prime to $n$. If above equality holds for all
natural $m$ and $n$ function called completely additive.

To define an additive function it is enough to define how to
calculate a function on power of primes.
If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then: 

$$ f(n) = \sum_{i=1}^{k} f({p_i}^{a_i}) $$


<a id="orgb2509ad"></a>

## Multiplicative functions

Multiplicative function is a function not equal to zero for all n
for which 

$$ f(mn) = f(m)f(n) $$

if $m$ relatively prime to $n$. If above equality holds for all
natural $m$ and $n$ function called completely multiplicative.

To define multiplicative function it is enough to define how to
calculate a function on power of primes. If $n = p_1^{a_1} p_2^{a_2}
\dots p_k^{a_k}$ then:

$$ f(n) = \prod_{i=1}^{k} f({p_i}^{a_i}) $$


<a id="orga96c70b"></a>

## Higher order function for define multiplicative and additive functions

As we have seen, to define either multiplicative or additive function
it is enough define function on power of a prime.  There is helper
function `reduce-on-prime-count` which provide a way to define a
function on power of a prime. The first parameter of
`reduce-on-prime-count` is reduce function which usually `*` for
multiplicative function and usually `+` for additive function, but
custom reduce function also acceptable.

For instance, we can define function which calculate number of
divisors of integer `n`. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ count of divisors of
number `n` can be calculated by formula:

$$ \sigma_0(n) = \prod_{i=1}^{k} (a_i + 1) $$

With helper function it can be defined as

    (def my-divisors-count
    (f/reduce-on-prime-count * (fn [p k] (inc k))))
    (my-divisors-count 6)

    class clojure.lang.Compiler$CompilerException

Of course there is predefined function `divisors-count`, but it
is an example how to define custom function.


<a id="org6373501"></a>

## Some additive functions


<a id="org8e3a5a5"></a>

### Count of distinct primes - $\omega$

Count of distinct primes is a number of distinct primes which
divides given $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then $\omega = k$.

    (f/primes-count-distinct (* 2 2 3))

    class clojure.lang.Compiler$CompilerException


<a id="orgf97b80c"></a>

### Total count of primes - $\Omega$

Total count of primes is a number of primes and power of primes
which divides $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then:

$$\Omega = a_1 + a_2 + \dots + a_k$$

    (f/primes-count-total (* 2 2 3))

    class clojure.lang.Compiler$CompilerException


<a id="orga9de90f"></a>

## Some multiplicative functions


<a id="orgb9dc070"></a>

### Mobius function - $\mu$.

Mobius function defined as:

$$ \mu(n) = \begin{cases}
1        &  \quad \text{if } n = 1 \\
(-1)^k   &  \quad \text{if } n \text{ product of distinct primes} \\
0        &  \quad \text{otherwise}
\end{cases} $$

For example, $\mu(6)=\mu(2 \cdot 3)=1$

    (f/mobius 6)

    class clojure.lang.Compiler$CompilerException


<a id="org26ce038"></a>

### Euler totient function - $\phi$

Euler totient function  is a count of numbers relative  prime to given
number `n`.  Totient function can be calculated by formula:

$$ \phi(n) = \prod_{p|n} (p^a - p^{a-1}) $$

For example, count of numbers relative prime to $6$ are $1$ and $5$, so $\phi(6) = 2$

    (f/totient 6)

    class clojure.lang.Compiler$CompilerException


<a id="orgf5a17d7"></a>

### Unit function - $\epsilon$

Unit function defined as

$$ \epsilon(n) = \begin{cases}
1,&  \text{if } n = 1 \\
0,&  \text{if } n > 1
\end{cases} $$

    (f/unit 6)

    class clojure.lang.Compiler$CompilerException


<a id="org49a33a6"></a>

### Constant one function - $1$

$$ 1(n) = 1 $$

    (f/one 6)

    class clojure.lang.Compiler$CompilerException


<a id="org0f1474e"></a>

### Divisors count - $\sigma_0$

Divisors count is number of divisors which divides given number $n$.

$$ \sigma_0(n) = \sum_{d|n} 1 $$

For example, number $64$ has $4$ divisors, namely $1,2,3,6$, so $\sigma_0(6)=4$

    (f/divisors-count 6)

    class clojure.lang.Compiler$CompilerException


<a id="org3d9dde0"></a>

### Divisors sum - $\sigma_1$

$$ \sigma_1(n) = \sum_{d | n} d $$

For number 6 it is $12 = 1 + 2 + 3 + 6$

    (f/divisors-sum 6)

    class clojure.lang.Compiler$CompilerException


<a id="org1d1a010"></a>

### Divisors square sum

$$ \sigma_2(n) = \sum_{d | n} d^2 $$

For number 6 it is $50 = 1^2 + 2^2 + 3^2 + 6^2$

    (f/divisors-square-sum 6)

    class clojure.lang.Compiler$CompilerException


<a id="org457286f"></a>

### Divisors higher order function - $\sigma_{x}$

In general $\sigma_x$ function is a sum of x-th powers divisors of given n

$$ \sigma_x(n) = \sum_{ d | n} d^x $$

If $x \ne 0$ $\sigma_x$ can be calculated by formula:

$$ \sigma_{x}(n) = \prod_{i=1}^{k} \frac {p_i^{(a_i+1)x}} {p_i^x - 1} $$

and if $x = 0$ by formula:

$$ \sigma_{0}(n) = \prod_{i=1}^{k} (a_i + 1) $$

There is higher order function `divisors-sum-x` which
accept `x` and return appropriate function.

    (def my-divisors-square-sum (f/divisors-sum-x 2))


<a id="org1b0873b"></a>

### Liouville - $\lambda$

Liouville function can be defind by formula:

$$\lambda(n) = (-1)^{\Omega(n)}$$

where [$\Omega$](#orgf97b80c) have been descibed above.

    (f/liouville (* 2 3)) 

    class clojure.lang.Compiler$CompilerException


<a id="org5d7d34f"></a>

## Some other arithmetic functions


<a id="org3c75d8a"></a>

### Mangoldt - $\Lambda$

$$\Lambda(n) = \begin{cases}
   \log p,& \text{if $n$ is power of prime i.e. $n = p^k$} \\
   0,& \text{otherwise} 
\end{cases}$$

For example $\Lambda(8) = \log 2$, $\Lambda(6) = 0$  

    (f/mangoldt 2)

    class clojure.lang.Compiler$CompilerException

    (f/mangoldt 6)

    class clojure.lang.Compiler$CompilerException


<a id="org374c279"></a>

### Chebyshev functions $\theta$ and $\psi$

There are two Chebyshev functions, one $\theta$ is defined as

$$\theta(x) = \sum_{p \le x} \log p$$

second $\psi$ defined as

$$\psi = \sum_{n \le x} {\Lambda(n)} $$

where [$\Lambda$](#org3c75d8a) have been described above

    (f/chebyshev-first 2)

    class clojure.lang.Compiler$CompilerException

    (f/chebyshev-second 2)

    class clojure.lang.Compiler$CompilerException


<a id="org0bcda65"></a>

## Dirichlet convolution

For two arithmetic functions $f$ and $g$ Dirichlet convolution is a
new arithmetic function defined as

$$ (f*g)(n) = \sum_{d | n} f(d)g(\frac{n}{d}) $$

Dirichlet convolution is associative

$$ (f * g) * h = f * (g * h) $$

Commutative

$$ f * g = g * f $$

Has identify

$$ f * \epsilon = \epsilon * f = f $$

For every $f$, which $f(1) \ne 0$ exists inverse function $f^{-1}$
such that $f * f^{-1} = \epsilon$. This inverse function called
Dirichlet inverse and can by calculated recursively by formula:

$$ f^{-1}(n) = \begin{cases}
\frac{1}{f(1)} & \quad \text{if } n = 1  \\
\frac{-1}{f(1)}\sum_{ \substack{d | n\\
                                d < n}} f(\frac{n}{d}) f^{-1}(d)
               & \quad n \ge 1
\end{cases} $$

For example, $1(n) * 1(n) = \sigma_0$

    (f/f=
       (f/d-* f/one f/one)
       f/divisors-count
    )

    class clojure.lang.Compiler$CompilerException

Dirichlet convolution is associative so clojure method support more than two
function as parameter of `f*`

    (f/f=
      (f/d-* f/mobius f/one f/mobius f/one)
      f/unit
    )

    class clojure.lang.Compiler$CompilerException

Another example, functions $\mu(n)$ and $1(n)$ are inverse of each other

    (f/f= (f/d-inv f/one) f/mobius)

    class clojure.lang.Compiler$CompilerException

    (f/f= (f/d-inv f/mobius) f/one)

    class clojure.lang.Compiler$CompilerException

Function `d-inv` defined as recursive function, it may
execute slow. But inverse of completely multiplicative function $f(n)$
is $f(n) \mu(n)$(usual multiplication), for instance inverse
of identity function, let's denote it $N(n)$ is $N(n) \mu(n)$

    (f/f=
     (f/d-* 
        #(* (identity %) (f/mobius %))
        identity
     )
     f/unit)

    class clojure.lang.Compiler$CompilerException

