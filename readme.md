
# Table of Contents

1.  [About](#org7394127)
2.  [Notation](#org5b0ae31)
3.  [Some basic functions](#org976742a)
    1.  [Power function](#org446ba72)
    2.  [Sign function](#org493242a)
    3.  [Order function](#org487a64e)
    4.  [The greatest common divisor](#orgc59f317)
4.  [Performance and cache](#orga30ac8b)
5.  [Primes](#orga9968e5)
6.  [Integer factorization](#orga59ea89)
7.  [Divisors](#org2fa9ef1)
8.  [Arithmetical functions](#org22e65af)
    1.  [Function equality](#org331fe5d)
    2.  [Additive functions](#org70251fd)
    3.  [Multiplicative functions](#org111c3a5)
    4.  [Higher order function for define multiplicative and additive functions](#org02caf23)
    5.  [Some additive functions](#org8270240)
        1.  [Count of distinct primes - $\omega$](#orgbfeebb6)
        2.  [Total count of primes - $\Omega$](#org2b210cc)
    6.  [Some multiplicative functions](#org46d7156)
        1.  [Mobius function - $\mu$.](#org13a96a8)
        2.  [Euler totient function - $\phi$](#orgd3df2fe)
        3.  [Unit function - $\epsilon$](#orgc9068d9)
        4.  [Constant one function - $1$](#orga98029a)
        5.  [Divisors count - $\sigma_0$](#org0137656)
        6.  [Divisors sum - $\sigma_1$](#org24306ec)
        7.  [Divisors square sum](#orga507709)
        8.  [Divisors higher order function - $\sigma_{x}$](#orga2892b5)
        9.  [Liouville - $\lambda$](#org7f24dd9)
    7.  [Some other arithmetic functions](#orgfa032d3)
        1.  [Mangoldt - $\Lambda$](#org09ef779)
        2.  [Chebyshev functions $\theta$ and $\psi$](#orgafdee60)
    8.  [Dirichlet convolution](#org1bc2cff)



<a id="org7394127"></a>

# About

This project cover some topics in number theory, especially arithmetic
functions, further more multiplicative functions. There are set of
well known arithmetic functions and one can define custom arithmetic
functions.

I wrote this document `readme.org` with Emacs Org Mode. Then I
generate markdown file `readme.md` with Org Mode export to markdown
`C-c C-e m m`, and generate pdf file `readme.odf` with Org Mode export
to pdf `C-c C-e l p`.  Github by default show `readme.md` if a project
has such file.  It looks enough good, even math equation is
supported. But I see some issue with greek characters in table of
content. If it is a problem `readme.pdf` looks better. Emacs file
"make.el" can be used to export markdown and pdf with one call. I use
Emacs babel for clojure to produce real output inside the document.

In this document I load number theory packages as: 

    (require '[vk.ntheory.basic :as b])
    (require '[vk.ntheory.primes :as p])
    (require '[vk.ntheory.ar-func :as f])
    (require '[vk.ntheory.congruence :as c])
    (require '[clojure.math :as math])

So below I will use above aliases.


<a id="org5b0ae31"></a>

# Notation

-   $\mathbf N$ - Natural numbers, positive integers $1,2,3,\dots$
-   $\mathbf C$ - Complex numbers
-   $\mathbf Z$ - Integers $\dots -3, -2, -1, 0, 1, 2, 3, \dots$


<a id="org976742a"></a>

# Some basic functions


<a id="org446ba72"></a>

## Power function

Clojure has built-in `clojure.math/pow` function, but it return
`java.lang.Double`. The library provide integer analog

    (b/pow 2 3)

    8


<a id="org493242a"></a>

## Sign function

    (mapv b/sign [(- 5) 10 0])

    [-1 1 0]


<a id="org487a64e"></a>

## Order function

Order function $ord_p(n)$ is a greatest power of $p$ divides $n$

    (b/order 2 24)

    3


<a id="orgc59f317"></a>

## The greatest common divisor

The greatest common divisor of two integer $a$ and $b$ is an positive
integer $d$ which divide $a$ and $b$ and any other common divisor `a`
and $b$ divides $d$.

    (b/gcd 12 18)

    6

For convenience `(gcd 0 0)` is `0`.

Furthermore, if for any two integers $a$ and $b$ exists integers `s`
and $t$ such that $a s + b t = d$ , where d is the greatest common
divisor. For example, $6 = 12 (-1) + 18 (1)$

    (b/gcd-extended 12 18)

    [6 -1 1]


<a id="orga30ac8b"></a>

# Performance and cache

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


<a id="orga9968e5"></a>

# Primes

`primes` function returns prime numbers which not exceeds given `n`.

    (p/primes 30)

    (2 3 5 7 11 13 17 19 23 29)


<a id="orga59ea89"></a>

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


<a id="org2fa9ef1"></a>

# Divisors

For get list of all divisors of number `n` there is `divisor`
function. List of divisors is unordered.

    (f/divisors 30)

    (1 2 3 6 5 10 15 30)


<a id="org22e65af"></a>

# Arithmetical functions

Arithmetical function is an any function which accept natural number
and return complex number $f: \mathbf N \to \mathbf C$. The library mostly works
with functions which also returns integer $f: \mathbf N \to \mathbf Z$.


<a id="org331fe5d"></a>

## Function equality

Two arithmetical function $f$ and $g$ are equal if $f(n)=g(n)$ for all
natual $n$. There is helper function `f-equlas` which compare two
functions on some sequence of natual numbers. Function `f-equals`
accept two functions and optionally sequence of natural numbers. There
is a default for sequence of natural numbers, it is a variable
`default-natural-sample`, which is currently `range(1,100)`.

If we like identify does two function `f` and `g` equals on some
sequence of natural number we can for example do next:

    ;; Let we have some f and g
    (def f identity)
    (def g (constantly 1))
    ;; Then we able to check does those functions are equals
    (f/f-equals f g)
    (f/f-equals f g (range 1 1000))
    (f/f-equals f g (filter even? (range 1 100)))


<a id="org70251fd"></a>

## Additive functions

Additive function is a function for which

$$ f(mn) = f(m) + f(n)$$

if $m$ relatively prime to $n$. If above equality holds for all
natural $m$ and $n$ function called completely additive.

To define an additive function it is enough to define how to
calculate a function on power of primes.
If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then: 

$$ f(n) = \sum_{i=1}^{k} f({p_i}^{a_i}) $$


<a id="org111c3a5"></a>

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


<a id="org02caf23"></a>

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

    4

Of course there is predefined function `divisors-count`, but it
is an example how to define custom function.


<a id="org8270240"></a>

## Some additive functions


<a id="orgbfeebb6"></a>

### Count of distinct primes - $\omega$

Count of distinct primes is a number of distinct primes which
divides given $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then $\omega = k$.

    (f/primes-count-distinct (* 2 2 3))

    2


<a id="org2b210cc"></a>

### Total count of primes - $\Omega$

Total count of primes is a number of primes and power of primes
which divides $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then:

$$\Omega = a_1 + a_2 + \dots + a_k$$

    (f/primes-count-total (* 2 2 3))

    3


<a id="org46d7156"></a>

## Some multiplicative functions


<a id="org13a96a8"></a>

### Mobius function - $\mu$.

Mobius function defined as:

$$ \mu(n) = \begin{cases}
1        &  \quad \text{if } n = 1 \\
(-1)^k   &  \quad \text{if } n \text{ product of distinct primes} \\
0        &  \quad \text{otherwise}
\end{cases} $$

For example, $\mu(6)=\mu(2 \cdot 3)=1$

    (f/mobius 6)

    1


<a id="orgd3df2fe"></a>

### Euler totient function - $\phi$

Euler totient function  is a count of numbers relative  prime to given
number `n`.  Totient function can be calculated by formula:

$$ \phi(n) = \prod_{p|n} (p^a - p^{a-1}) $$

For example, count of numbers relative prime to $6$ are $1$ and $5$, so $\phi(6) = 2$

    (f/totient 6)

    2


<a id="orgc9068d9"></a>

### Unit function - $\epsilon$

Unit function defined as

$$ \epsilon(n) = \begin{cases}
1,&  \text{if } n = 1 \\
0,&  \text{if } n > 1
\end{cases} $$

    (f/unit 6)

    0


<a id="orga98029a"></a>

### Constant one function - $1$

$$ 1(n) = 1 $$

    (f/one 6)

    1


<a id="org0137656"></a>

### Divisors count - $\sigma_0$

Divisors count is number of divisors which divides given number $n$.

$$ \sigma_0(n) = \sum_{d|n} 1 $$

For example, number $64$ has $4$ divisors, namely $1,2,3,6$, so $\sigma_0(6)=4$

    (f/divisors-count 6)

    4


<a id="org24306ec"></a>

### Divisors sum - $\sigma_1$

$$ \sigma_1(n) = \sum_{d | n} d $$

For number 6 it is $12 = 1 + 2 + 3 + 6$

    (f/divisors-sum 6)

    12


<a id="orga507709"></a>

### Divisors square sum

$$ \sigma_2(n) = \sum_{d | n} d^2 $$

For number 6 it is $50 = 1^2 + 2^2 + 3^2 + 6^2$

    (f/divisors-square-sum 6)

    50


<a id="orga2892b5"></a>

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


<a id="org7f24dd9"></a>

### Liouville - $\lambda$

Liouville function can be defind by formula:

$$\lambda(n) = (-1)^{\Omega(n)}$$

where [$\Omega$](#org2b210cc) have been descibed above.

    (f/liouville (* 2 3)) 

    1


<a id="orgfa032d3"></a>

## Some other arithmetic functions


<a id="org09ef779"></a>

### Mangoldt - $\Lambda$

$$\Lambda(n) = \begin{cases}
   \log p,& \text{if $n$ is power of prime i.e. $n = p^k$} \\
   0,& \text{otherwise} 
\end{cases}$$

For example $\Lambda(8) = \log 2$, $\Lambda(6) = 0$  

    (f/mangoldt 2)

    0.6931471805599453

    (f/mangoldt 6)

    0


<a id="orgafdee60"></a>

### Chebyshev functions $\theta$ and $\psi$

There are two Chebyshev functions, one $\theta$ is defined as

$$\theta(x) = \sum_{p \le x} \log p$$

second $\psi$ defined as

$$\psi = \sum_{n \le x} {\Lambda(n)} $$

where [$\Lambda$](#org09ef779) have been described above

    (f/chebyshev-first 2)

    0.6931471805599453

    (f/chebyshev-second 2)

    0.6931471805599453


<a id="org1bc2cff"></a>

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

    (f/f-equals
       (f/d-* f/one f/one)
       f/divisors-count
    )

    class clojure.lang.Compiler$CompilerException

Dirichlet convolution is associative so clojure method support more than two
function as parameter of `f*`

    (f/f-equals
      (f/d-* f/mobius f/one f/mobius f/one)
      f/unit
    )

    class clojure.lang.Compiler$CompilerException

Another example, functions $\mu(n)$ and $1(n)$ are inverse of each other

    (f/f-equals (f/d-inv f/one) f/mobius)

    class clojure.lang.Compiler$CompilerException

    (f/f-equals (f/d-inv f/mobius) f/one)

    class clojure.lang.Compiler$CompilerException

Function `d-inv` defined as recursive function, it may
execute slow. But inverse of completely multiplicative function $f(n)$
is $f(n) \mu(n)$(usual multiplication), for instance inverse
of identity function, let's denote it $N(n)$ is $N(n) \mu(n)$

    (f/f-equals
     (f/d-* 
        #(* (identity %) (f/mobius %))
        identity
     )
     f/unit)

    class clojure.lang.Compiler$CompilerException

