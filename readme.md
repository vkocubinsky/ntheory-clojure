
# Table of Contents

1.  [About](#org72b523b)
2.  [Notation](#orgd645ca2)
3.  [Performance and cache](#org4a91474)
4.  [Prime numbers](#org901c706)
5.  [Integer factorization](#org9def528)
6.  [Divisors](#orgd44ef7d)
7.  [Arithmetical functions](#orge5b4e22)
8.  [Function equality](#org9e96a77)
9.  [Additive functions](#org15d1931)
10. [Multiplicative functions](#org3161317)
11. [Higher order function for define multiplicative and additive functions](#org9101339)
12. [Some additive functions](#orgf8047ae)
    1.  [Count of distinct primes - $\omega$](#org2c835d4)
    2.  [Total count of primes - $\Omega$](#orge3f1cb7)
13. [Some multiplicative functions](#org872ed1e)
    1.  [Mobius function - $\mu$.](#org88fbb92)
    2.  [Euler totient function - $\phi$](#org4f72496)
    3.  [Unit function - $\epsilon$](#org53135ae)
    4.  [Constant one function - $1$](#org8dfc004)
    5.  [Divisors count - $\sigma_0$](#orgbce56c4)
    6.  [Divisors sum - $\sigma_1$](#org5cbce57)
    7.  [Divisors square sum](#orgca18557)
    8.  [Divisors higher order function - $\sigma_{x}$](#org221f954)
    9.  [Liouville - $\lambda$](#org2b200e3)
14. [Some other arithmetic functions](#org25f5ad8)
    1.  [Mangoldt - $\Lambda$](#org54defe8)
    2.  [Chebyshev functions $\theta$ and $\psi$](#orgf1405e2)
15. [Dirichlet convolution](#orgca37855)



<a id="org72b523b"></a>

# About

This project cover some topics in number theory, especially arithmetic
functions, further more multiplicative functions. There are set of
well known arithmetic functions and one can define custom arithmetic
functions.

I wrote this document with Emacs Org Mode. Then I generated markdown
file with `pandoc` to show it nicely on github. I use Emacs babel to
produce real output inside the document.

In this document I load number theory package as: 

    (require '[vk.ntheory :as nt])
    (require '[clojure.math :as math])

So below I will use `nt` alias.


<a id="orgd645ca2"></a>

# Notation

$\mathbf N$ - Natural numbers, positive integers $1,2,3,\dots$
$\mathbf C$ - Complex numbers
$\mathbf Z$ - Integers $\dots -3, -2, -1, 0, 1, 2, 3, \dots$


<a id="org4a91474"></a>

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

    (nt/ldt-reset!)

    {:least-divisor-table , :primes , :upper 0}

Least prime divisor table is implementation details, but one can see
it:

    (nt/integer->factors-map 5); load first 10 natural numbers
    @ldt

    {:least-divisor-table [0, 1, 2, 3, 2, 5, 2, 7, 2, 3, 2],
     :primes (2 3 5 7),
     :upper 10}

For instance, for get least prime divisor of number 6 we need to get
element with index 6, which is 2. Index zero is not used, value for
index 1 is 1.


<a id="org901c706"></a>

# Prime numbers

`primes` function returns prime numbers which not exceeds given `n`.

    (nt/primes 30)

    (2 3 5 7 11 13 17 19 23 29)


<a id="org9def528"></a>

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

    (nt/integer->factors 360)
    (nt/factors->integer [2 2 2 3 3 5])

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-right" />
</colgroup>
<tbody>
<tr>
<td class="org-right">(2 2 2 3 3 5)</td>
</tr>


<tr>
<td class="org-right">360</td>
</tr>
</tbody>
</table>

2-nd factorization representation is ordered sequence of primes
splited by partitions by a prime:

    (nt/integer->factors-partitions 360)
    (nt/factors-partitions->integer [[2 2 2] [3 3] [5]])

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-right" />
</colgroup>
<tbody>
<tr>
<td class="org-right">((2 2 2) (3 3) (5))</td>
</tr>


<tr>
<td class="org-right">360</td>
</tr>
</tbody>
</table>

3-rd factorization representation is ordered sequence of pairs `[p
k]`, where `p` is a prime and `k` is a power of prime

    (nt/integer->factors-count 360)
    (nt/factors-count->integer [[2 3] [3 2] [5 1]])

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-right" />
</colgroup>
<tbody>
<tr>
<td class="org-right">([2 3] [3 2] [5 1])</td>
</tr>


<tr>
<td class="org-right">360</td>
</tr>
</tbody>
</table>

4-th factorization representation is very similar to 3-rd, but it
is a map. And it has the same inverse function as 3-rd.

    (nt/integer->factors-map 360)
    (nt/factors-count->integer {2 3, 3 2, 5 1})

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-right" />
</colgroup>
<tbody>
<tr>
<td class="org-right">{2 3, 3 2, 5 1}</td>
</tr>


<tr>
<td class="org-right">360</td>
</tr>
</tbody>
</table>

Implementation of factorization use least prime divisor table. To
factorize number `n` it is enough to calculate least divisor table
with size less or equals to $\sqrt n$. 


<a id="orgd44ef7d"></a>

# Divisors

For get list of all divisors of number `n` there is `divisor`
function. List of divisors is unordered.

    (nt/divisors 30)

    (1 2 3 6 5 10 15 30)


<a id="orge5b4e22"></a>

# Arithmetical functions

Arithmetical function is an any function which accept natural number
and return complex number $f: \mathbf N \to \mathbf C$. The library mostly works
with functions which also returns integer $f: \mathbf N \to \mathbf Z$.


<a id="org9e96a77"></a>

# Function equality

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
    (nt/f-equals f g)
    (nt/f-equals f g (range 1 1000))
    (nt/f-equals f g (filter even? (range 1 100)))


<a id="org15d1931"></a>

# Additive functions

Additive function is a function for which

$$ f(mn) = f(m) + f(n)$$

if $m$ relatively prime to $n$. If above equality holds for all
natural $m$ and $n$ function called completely additive.

To define an additive function it is enough to define how to
calculate a function on power of primes.
If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then: 

$$ f(n) = \sum_{i=1}^{k} f({p_i}^{a_i}) $$


<a id="org3161317"></a>

# Multiplicative functions

Multiplicative function is a function not equal to zero for all n
for which 

$$ f(mn) = f(m)f(n) $$

if $m$ relatively prime to $n$. If above equality holds for all
natural $m$ and $n$ function called completely multiplicative.

To define multiplicative function it is enough to define how to
calculate a function on power of primes. If $n = p_1^{a_1} p_2^{a_2}
\dots p_k^{a_k}$ then:

$$ f(n) = \prod_{i=1}^{k} f({p_i}^{a_i}) $$


<a id="org9101339"></a>

# Higher order function for define multiplicative and additive functions

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
    (nt/reduce-on-prime-count * (fn [p k] (inc k))))

    (my-divisors-count 6)

    class clojure.lang.Compiler$CompilerException

Of course there is predefined function `divisors-count`, but it
is an example how to define custom function.


<a id="orgf8047ae"></a>

# Some additive functions


<a id="org2c835d4"></a>

## Count of distinct primes - $\omega$

Count of distinct primes is a number of distinct primes which
divides given $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then $\omega = k$.

    (nt/primes-count-distinct (* 2 2 3))

    2


<a id="orge3f1cb7"></a>

## Total count of primes - $\Omega$

Total count of primes is a number of primes and power of primes
which divides $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then:

$$\Omega = a_1 + a_2 + \dots + a_k$$

    (nt/primes-count-total (* 2 2 3))

    3


<a id="org872ed1e"></a>

# Some multiplicative functions


<a id="org88fbb92"></a>

## Mobius function - $\mu$.

Mobius function defined as:

$$ \mu(n) = \begin{cases}
1        &  \quad \text{if } n = 1 \\
(-1)^k   &  \quad \text{if } n \text{ product of distinct primes} \\
0        &  \quad \text{otherwise}
\end{cases} $$

For example, $\mu(6)=\mu(2 \cdot 3)=1$

    (nt/mobius 6)

    1


<a id="org4f72496"></a>

## Euler totient function - $\phi$

Euler totient function  is a count of numbers relative  prime to given
number `n`.  Totient function can be calculated by formula:

$$ \phi(n) = \prod_{p|n} (p^a - p^{a-1}) $$

For example, count of numbers relative prime to $6$ are $1$ and $5$, so $\phi(6) = 2$

    (nt/totient 6)

    2


<a id="org53135ae"></a>

## Unit function - $\epsilon$

Unit function defined as

$$ \epsilon(n) = \begin{cases}
1,&  \text{if } n = 1 \\
0,&  \text{if } n > 1
\end{cases} $$

    (nt/unit 6)

    0


<a id="org8dfc004"></a>

## Constant one function - $1$

$$ 1(n) = 1 $$

    (nt/one 6)

    1


<a id="orgbce56c4"></a>

## Divisors count - $\sigma_0$

Divisors count is number of divisors which divides given number $n$.

$$ \sigma_0(n) = \sum_{d|n} 1 $$

For example, number $64$ has $4$ divisors, namely $1,2,3,6$, so $\sigma_0(6)=4$

    (nt/divisors-count 6)

    4


<a id="org5cbce57"></a>

## Divisors sum - $\sigma_1$

$$ \sigma_1(n) = \sum_{d | n} d $$

For number 6 it is $12 = 1 + 2 + 3 + 6$

    (nt/divisors-sum 6)

    12


<a id="orgca18557"></a>

## Divisors square sum

$$ \sigma_2(n) = \sum_{d | n} d^2 $$

For number 6 it is $50 = 1^2 + 2^2 + 3^2 + 6^2$

    (nt/divisors-square-sum 6)

    50


<a id="org221f954"></a>

## Divisors higher order function - $\sigma_{x}$

In general $\sigma_x$ function is a sum of x-th powers divisors of given n

$$ \sigma_x(n) = \sum_{ d | n} d^x $$

If $x \ne 0$ $\sigma_x$ can be calculated by formula:

$$ \sigma_{x}(n) = \prod_{i=1}^{k} \frac {p_i^{(a_i+1)x}} {p_i^x - 1} $$

and if $x = 0$ by formula:

$$ \sigma_{0}(n) = \prod_{i=1}^{k} (a_i + 1) $$

There is higher order function `divisors-sum-x` which
accept `x` and return appropriate function.

    (def my-divisors-square-sum (nt/divisors-sum-x 2))


<a id="org2b200e3"></a>

## Liouville - $\lambda$

Liouville function can be defind by formula:

$$\lambda(n) = (-1)^{\Omega(n)}$$

where [$\Omega$](#orge3f1cb7) have been descibed above.

    (nt/liouville (* 2 3)) 

    1


<a id="org25f5ad8"></a>

# Some other arithmetic functions


<a id="org54defe8"></a>

## Mangoldt - $\Lambda$

$$\Lambda(n) = \begin{cases}
   \log p,& \text{if $n$ is power of prime i.e. $n = p^k$} \\
   0,& \text{otherwise} 
\end{cases}$$

For example $\Lambda(8) = \log 2$, $\Lambda(6) = 0$  

    (nt/mangoldt 2)
    (nt/mangoldt 6)

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-right" />
</colgroup>
<tbody>
<tr>
<td class="org-right">0.6931471805599453</td>
</tr>


<tr>
<td class="org-right">0</td>
</tr>
</tbody>
</table>


<a id="orgf1405e2"></a>

## Chebyshev functions $\theta$ and $\psi$

There are two Chebyshev functions, one $\theta$ is defined as

$$\theta(x) = \sum_{p \le x} \log p$$

second $\psi$ defined as

$$\psi = \sum_{n \le x} {\Lambda(n)} $$

where [$\Lambda$](#org54defe8) have been described above

    (nt/chebyshev-first 2)
    (nt/chebyshev-second 2)

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-right" />
</colgroup>
<tbody>
<tr>
<td class="org-right">0.6931471805599453</td>
</tr>


<tr>
<td class="org-right">0.6931471805599453</td>
</tr>
</tbody>
</table>


<a id="orgca37855"></a>

# Dirichlet convolution

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

    (nt/f-equals
       (nt/dirichlet-convolution nt/one nt/one)
       nt/divisors-count
    )

    true

Dirichlet convolution is associative so clojure method support more than two
function as parameter of `f*`

    (nt/f-equals
      (nt/dirichlet-convolution nt/mobius nt/one nt/mobius nt/one)
      nt/unit
    )

    true

Another example, functions $\mu(n)$ and $1(n)$ are inverse of each other

    (nt/f-equals (nt/dirichlet-inverse nt/one) nt/mobius)
    (nt/f-equals (nt/dirichlet-inverse nt/mobius) nt/one)

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-left" />
</colgroup>
<tbody>
<tr>
<td class="org-left">true</td>
</tr>


<tr>
<td class="org-left">true</td>
</tr>
</tbody>
</table>

