
# Table of Contents

1.  [About](#org3eca33b)
2.  [Notation](#org4fe0bd9)
3.  [Some basic functions `vk.ntheory.basic`](#orgf3f9699)
    1.  [Check functions](#org418126b)
    2.  [Some predicates](#org4a26566)
    3.  [Operations in $\mathbf{Z}/m\mathbf{Z}$](#org929fbd2)
    4.  [Power function](#org08dd85b)
    5.  [Order function](#org577100f)
    6.  [Sign function](#orgb61bbad)
    7.  [The greatest common divisor](#org9d50b4f)
    8.  [The least common multiple](#orgdb57c0e)
4.  [Primes and Integer Factorization `vk.ntheory.primes`](#orgda40441)
    1.  [Performance and cache](#orgea60d57)
    2.  [Primes](#org600ffc4)
    3.  [Integer factorization](#orgd96a522)
    4.  [Check functions](#orgda7c2ac)
5.  [Arithmetical functions `vk.ntheory.ar-func`](#org26fb99c)
    1.  [Divisors](#org382c927)
    2.  [Arithmetical function](#org5ca93dd)
    3.  [Function equality](#org6a21d5f)
    4.  [Additive functions](#org8f566bb)
    5.  [Multiplicative functions](#org867cd0f)
    6.  [Higher order function for define multiplicative and additive functions](#orgbf11a62)
    7.  [Some additive functions](#org122fc6a)
        1.  [Count of distinct primes - $\omega$](#orgcc65a65)
        2.  [Total count of primes - $\Omega$](#org71a1c71)
    8.  [Some multiplicative functions](#orgabc3c7a)
        1.  [Mobius function - $\mu$.](#orge685394)
        2.  [Euler totient function - $\phi$](#org77c9a98)
        3.  [Unit function - $\epsilon$](#orgf1f2add)
        4.  [Constant one function - $1$](#orgad8352b)
        5.  [Divisors count - $\sigma_0$](#org4e33567)
        6.  [Divisors sum - $\sigma_1$](#org4baa190)
        7.  [Divisors square sum](#org5a37f49)
        8.  [Divisors higher order function - $\sigma_{x}$](#orge3b04bb)
        9.  [Liouville - $\lambda$](#org3431d5d)
    9.  [Some other arithmetic functions](#org72502ac)
        1.  [Mangoldt - $\Lambda$](#org829afae)
    10. [Dirichlet convolution](#orge46a60a)
6.  [Conguences `vk.ntheory.congruence`](#org0e795f2)
7.  [Primitive Roots `vk.ntheory.primitive-roots`](#org89f96eb)
8.  [Quadratic residies ~vk.ntheory.quadratic-residue](#orga891dc7)



<a id="org3eca33b"></a>

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
has such file.  Githib markdown looks enough good, even math equation
is supported. But I see some issues with greek characters in table of
content and in link text. If it is a problem `readme.pdf` looks
better. I use Emacs babel for clojure to produce real output inside
the document.

Many application require integer factorization. Integer factorization
is relatively expensive procedure. Most straightforward way to
factorize integer $n$ is try to divide $n$ to numbers $2,3,4, \dots
\sqrt{n}$. There is some optimization of this procedure. For instance
exclude from candidates multiple of $2$, or multiple of $2$ and
multiple of $3$. It is enough good strategy to factorize one
number. But if need factorize many numbers more fast way to factorize
number is precalculate table of first $N$ primes and try to divide
number $n$ to primes $p_1,p_2, \dots, p_n \le \sqrt{n}$.  Table of
first $N$ primes can be caclulated with Sieve of Eratosthenes. There
is a way to improve performance of factorization. With slightly
modified Sieve of Eratosthenes procedure we can precalculate least
prime divisor table. This table keep least prime divisor for every
integer up to $N$. This table works as linked list, in fact we know
all divisors of any number up to $N$ and to factorize integer we need
just iterate over "linked list" to build output data structure. But
least prime divisor table require more memory compare to Sieve of
Eratosthenes.  There is a way to improve performance, it is another
modification of Sieve of Eratosthenes procedure to build full
factorization for all numberS $1,2,\dots,N$.  It require much more
memory for keep table of full factorization and it require more time
for calculate full factorization table, compare to build least prime
divisor table. 

The purpose of this library/tool is provide fast way to play with
relatively small numbers(millions) and I made a decision to use least
prime divisor table as a best compromise between build table time,
required memory for table, integer factorization time. So now
factorization is cheap, but it require 4 bytes per number or 4MB
for 1000000.

In this document I load number theory packages as: 

    (require '[vk.ntheory.basic :as b])
    (require '[vk.ntheory.primes :as p])
    (require '[vk.ntheory.ar-func :as af])
    (require '[vk.ntheory.congruence :as c])
    (require '[vk.ntheory.primitive-roots :as pr])
    (require '[clojure.math :as math])

So below I will use above aliases.


<a id="org4fe0bd9"></a>

# Notation

-   $\mathbf N$ - Natural numbers, positive integers $1,2,3,\dots$
-   $\mathbf C$ - Complex numbers
-   $\mathbf Z$ - Integers $\dots -3, -2, -1, 0, 1, 2, 3, \dots$
-   $\mathbf Z/m\mathbf Z$ - Ring of integers modulo $m$
-   $(a,b)$ - the greatest common divisor of $a$ and $b$
-   $[a,b]$ - the least common multiple of $a$ and $b$


<a id="orgf3f9699"></a>

# Some basic functions `vk.ntheory.basic`

This section cover namespace `vk.ntheory.basic`. It contains some
common functions, which can be used directly or by other namespaces.

    (require '[vk.ntheory.basic :as b])


<a id="org418126b"></a>

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


<a id="org4a26566"></a>

## Some predicates

Function `divides?` determine does one number divides another.

    (b/divides? 2 8)

    true


<a id="org929fbd2"></a>

## Operations in $\mathbf{Z}/m\mathbf{Z}$

Similar to addition function `+` and multiplication function `*` there
defined addition modulo m `m+` and multiplication modulo m `m*`
functions. First argument of these functions is a modulo.

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
      (m5+ 1 (m5* 2 4)))

    4

There is another helpful function modulo m - exponentiation. It is a
fast binary exponentiation algorithm described in D.Knuth, The Art of
Computer Programming, Volume II.

For instance, $101^{900} \equiv 701 \pmod{997}$

    (b/m** 997 101 900)

    701


<a id="org08dd85b"></a>

## Power function

Clojure has built-in `clojure.math/pow` function, but it return
`java.lang.Double`. The library provide integer analog.

    (b/pow 2 3)

    8


<a id="org577100f"></a>

## Order function

Order function $ord_p(n)$ is a greatest power of $p$ divides $n$. For instance,
$2^3 | 24$, but $2^4 \nmid 24$, so $ord_2(24) = 3$

    (b/order 2 24)

    3


<a id="orgb61bbad"></a>

## Sign function

The `sign` function

$$sign(n) = \begin{cases}
-1 & \quad \text{if } x < 0 \\
0  & \quad \text{if } x = 0 \\
1  & \quad \text{if } x > 0
\end{cases}
$$

    (mapv b/sign [(- 5) 10 0])

    [-1 1 0]


<a id="org9d50b4f"></a>

## The greatest common divisor

The greatest common divisor of two integer $a$ and $b$ is an positive
integer $d$ which divides $a$ and $b$ , and any other common divisor $a$
and $b$ divides $d$.

    (b/gcd 12 18)

    6

The greatest common divisors of $a$ and $b$ is denoted by $(a,b)$.
For convenience $(0, 0) = 0$.

Furthermore, if for any two integers $a$ and $b$ exists integers $s$
and $t$ such that $a s + b t = d$ , where $d$ is the greatest common
divisor. For example, $6 = 12 (-1) + 18 (1)$

    (b/gcd-extended 12 18)

    [6 -1 1]


<a id="orgdb57c0e"></a>

## The least common multiple

The least common multiple of two integers $a$ and $b$ is denoted by
$[a, b]$, is an smallest integer which is multiple of $a$ and $b$. 
It defined in code as follows:

$$[a,b] = \begin{cases}
\frac{|ab|}{(a,b)} & \quad \text{if } a \ne 0 \text{ and } b \ne 0 \\
0                  & \quad \text{if } a = 0 \text{ or } b = 0 
\end{cases}
$$

    (b/lcm 12 18) 

    36


<a id="orgda40441"></a>

# Primes and Integer Factorization `vk.ntheory.primes`

This section cover namespace `vk.ntheory.primes`. It primary designed
for integer factorization and get list of primes. One can use `primes`
namespace as:

    (require '[vk.ntheory.primes :as p])


<a id="orgea60d57"></a>

## Performance and cache

This library is designed to work with realtive small integers. Library
keep in cache least prime divisor table for fast integer
factorization.  Least prime divisor of an positive integer is least
divisor, but not `1`.  Cache grows automatically. The strategy of
growing is extends cache to the least power of `10` more than required
number. For instance, if client asked to factorize number `18`, cache
grows to `100`, if client asked to factorize number `343`, cache grows
to `1000`. List of primes also cached and recalculated together with
least prime divisor table. Recalculation is not incremental, but every
recalculation of least prime divisor table make a table which is in
`10` times more than previous, and time for previous calculation is
`10` times less than for new one. So we can say that recalculation
spent almost all time for recalculate latest least prime divisor
table.

Internally, least prime divisor table is java array of `int`, so to store
least prime divisor table for first `1 000 000` number approximately `4M`
memory is required, `4` bytes per number.

There is a limit for max size of least prime divisor table. It is value of
`max-int`:

    p/max-int

    1000000

Cache can be reset:

    (p/cache-reset!)

    {:least-divisor-table , :primes , :upper 0}

Least prime divisor table is implementation details, but one can see
it:

    ;; load first 10 numbers into cache
    (p/int->factors-map 5)
    (deref p/cache)

    {:least-divisor-table [0, 1, 2, 3, 2, 5, 2, 7, 2, 3, 2],
     :primes (2 3 5 7),
     :upper 10}

For number `n` least prime divisor table contains least prime divisor
of number `n` at index `n`.  For instance, least prime divisor of
number `6` is `2`. If number `n > 1` is a prime, least prime divisor
is `n` and conversely. So at index `7` least prime divisor table
contains `7`. Index zero is not used, index `1` is a special case and
value for index `1` is `1`.


<a id="org600ffc4"></a>

## Primes

`primes` function returns prime numbers which not exceeds given `n`.

    (p/primes 30)

    (2 3 5 7 11 13 17 19 23 29)


<a id="orgd96a522"></a>

## Integer factorization

Integer $p$ is a prime if

-   $p > 1$
-   has only divisors $1$ and $p$.

There is `prime?` function:

    (p/prime? 7)

    true

Integer $n$ is a composite number if

-   $n > 1$
-   has at least one proper divisor, i.e. divisor except $1$ and $p$

There is `composite?` function:

    (p/composite? 12)

    true

Integer $1$ is not a prime and is not a composite

    (p/unit? 1)

    true

So all natural numbers can be divided into 3 categories: prime,
composite, one.

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

And converse function is:

    (p/factors->int [2 2 2 3 3 5])

    360

2-nd factorization representation is ordered sequence of primes
splited by partitions by a prime:

    (p/int->factors-partitions 360)

    ((2 2 2) (3 3) (5))

And converse function is:

    (p/factors-partitions->int [[2 2 2] [3 3] [5]])

    360

3-rd factorization representation is ordered sequence of pairs `[p
k]`, where `p` is a prime and `k` is a power of prime:

    (p/int->factors-count 360)

    ([2 3] [3 2] [5 1])

And converse function is:

    (p/factors-count->int [[2 3] [3 2] [5 1]])

    360

4-th factorization representation is very similar to 3-rd, but it
is a map instead of sequence of pairs. 

    (p/int->factors-map 360)

    {2 3, 3 2, 5 1}

Conversion function is the same as for 3-rd representation:

    (p/factors-count->int {2 3, 3 2, 5 1})

    360

Implementation of factorization use least prime divisor
table. Actually least prime divisor table is a kind of linked list, to
get next least prime divisor of an integer `n` need just divide `n` on
least prime divisor `p`, and quotient `n/p` is an index of next least
prime divisor of integer `n/p` and therefore divisor `n`.


<a id="orgda7c2ac"></a>

## Check functions

Addition to `vk.nthery.basic` namespace, namespace `vk.ntheory.primes`
provides additional set of `check-*` functions:

-   `check-int-pos-max`
-   `check-int-non-neg-max`
-   `check-int-non-zero-max`

It is similar to `vk.ntheory.basic` check functions, but additionally check
that given number does not exceeds `max-int` constant. And there are some
more check functions:

-   `check-prime`
-   `check-odd-prime`


<a id="org26fb99c"></a>

# Arithmetical functions `vk.ntheory.ar-func`

This section cover namespace `vk.ntheory.primes`. It contains some
well known arithmetical functions and also functions which allow build
new arithmetical functions.

    (require '[vk.ntheory.ar-func :as af])


<a id="org382c927"></a>

## Divisors

For get list of all divisors of number `n` there is `divisor`
function. List of divisors is unordered.

    (af/divisors 30)

    (1 2 3 6 5 10 15 30)


<a id="org5ca93dd"></a>

## Arithmetical function

Arithmetical function is an any function which accept natural number
and return complex number $f: \mathbf N \to \mathbf C$. The library mostly works
with functions which also returns integer $f: \mathbf N \to \mathbf Z$.


<a id="org6a21d5f"></a>

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


<a id="org8f566bb"></a>

## Additive functions

Additive function is a function for which

$$ f(mn) = f(m) + f(n)$$

if $m$ relatively prime to $n$. If above equality holds for all
natural $m$ and $n$ function called completely additive.

To define an additive function it is enough to define how to
calculate a function on power of primes.
If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then: 

$$ f(n) = \sum_{i=1}^{k} f({p_i}^{a_i}) $$


<a id="org867cd0f"></a>

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


<a id="orgbf11a62"></a>

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
    (af/reduce-on-prime-count * (fn [p k] (inc k))))
    (my-divisors-count 6)

    4

Of course there is predefined function `divisors-count`, but it
is an example how to define custom function.


<a id="org122fc6a"></a>

## Some additive functions


<a id="orgcc65a65"></a>

### Count of distinct primes - $\omega$

Count of distinct primes is a number of distinct primes which
divides given $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then $\omega = k$.

    (af/primes-count-distinct (* 2 2 3))

    2


<a id="org71a1c71"></a>

### Total count of primes - $\Omega$

Total count of primes is a number of primes and power of primes
which divides $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then:

$$\Omega = a_1 + a_2 + \dots + a_k$$

    (af/primes-count-total (* 2 2 3))

    3


<a id="orgabc3c7a"></a>

## Some multiplicative functions


<a id="orge685394"></a>

### Mobius function - $\mu$.

Mobius function defined as:

$$ \mu(n) = \begin{cases}
1        &  \quad \text{if } n = 1 \\
(-1)^k   &  \quad \text{if } n \text{ product of distinct primes} \\
0        &  \quad \text{otherwise}
\end{cases} $$

For example, $\mu(6)=\mu(2 \cdot 3)=1$

    (af/mobius 6)

    1


<a id="org77c9a98"></a>

### Euler totient function - $\phi$

Euler totient function  is a count of numbers relative  prime to given
number `n`.  Totient function can be calculated by formula:

$$ \phi(n) = \prod_{p|n} (p^a - p^{a-1}) $$

For example, count of numbers relative prime to $6$ are $1$ and $5$, so $\phi(6) = 2$

    (af/totient 6)

    2


<a id="orgf1f2add"></a>

### Unit function - $\epsilon$

Unit function defined as

$$ \epsilon(n) = \begin{cases}
1,&  \text{if } n = 1 \\
0,&  \text{if } n > 1
\end{cases} $$

    (af/unit 6)

    0


<a id="orgad8352b"></a>

### Constant one function - $1$

$$ 1(n) = 1 $$

    (af/one 6)

    1


<a id="org4e33567"></a>

### Divisors count - $\sigma_0$

Divisors count is number of divisors which divides given number $n$.

$$ \sigma_0(n) = \sum_{d|n} 1 $$

For example, number $64$ has $4$ divisors, namely $1,2,3,6$, so $\sigma_0(6)=4$

    (af/divisors-count 6)

    4


<a id="org4baa190"></a>

### Divisors sum - $\sigma_1$

$$ \sigma_1(n) = \sum_{d | n} d $$

For number 6 it is $12 = 1 + 2 + 3 + 6$

    (af/divisors-sum 6)

    12


<a id="org5a37f49"></a>

### Divisors square sum

$$ \sigma_2(n) = \sum_{d | n} d^2 $$

For number 6 it is $50 = 1^2 + 2^2 + 3^2 + 6^2$

    (af/divisors-square-sum 6)

    50


<a id="orge3b04bb"></a>

### Divisors higher order function - $\sigma_{x}$

In general $\sigma_x$ function is a sum of x-th powers divisors of given n

$$ \sigma_x(n) = \sum_{ d | n} d^x $$

If $x \ne 0$ $\sigma_x$ can be calculated by formula:

$$ \sigma_{x}(n) = \prod_{i=1}^{k} \frac {p_i^{(a_i+1)x}} {p_i^x - 1} $$

and if $x = 0$ by formula:

$$ \sigma_{0}(n) = \prod_{i=1}^{k} (a_i + 1) $$

There is higher order function `divisors-sum-x` which
accept `x` and return appropriate function.

    (def my-divisors-square-sum (af/divisors-sum-x 2))


<a id="org3431d5d"></a>

### Liouville - $\lambda$

Liouville function can be defind by formula:

$$\lambda(n) = (-1)^{\Omega(n)}$$

where [$\Omega$](#org71a1c71) have been descibed above.

    (af/liouville (* 2 3)) 

    1


<a id="org72502ac"></a>

## Some other arithmetic functions


<a id="org829afae"></a>

### Mangoldt - $\Lambda$

$$\Lambda(n) = \begin{cases}
   \log p,& \text{if $n$ is power of prime i.e. $n = p^k$} \\
   0,& \text{otherwise} 
\end{cases}$$

For example $\Lambda(8) = \log 2$, $\Lambda(6) = 0$  

    (af/mangoldt 2)

    0.6931471805599453

    (af/mangoldt 6)

    0

1.  Chebyshev functions $\theta$ and $\psi$

    There are two Chebyshev functions, one $\theta$ is defined as
    
    $$\theta(x) = \sum_{p \le x} \log p$$
    
    second $\psi$ defined as
    
    $$\psi = \sum_{n \le x} {\Lambda(n)} $$
    
    where [$\Lambda$](#org829afae) have been described above
    
        (af/chebyshev-first 2)
    
        0.6931471805599453
    
        (af/chebyshev-second 2)
    
        0.6931471805599453


<a id="orge46a60a"></a>

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

    (af/f=
       (af/d* af/one af/one)
       af/divisors-count
    )

    true

Dirichlet convolution is associative so clojure method support more than two
function as parameter of `f*`

    (af/f=
      (af/d* af/mobius af/one af/mobius af/one)
      af/unit
    )

    true

Another example, functions $\mu(n)$ and $1(n)$ are inverse of each other

    (f/f= (f/d-inv f/one) f/mobius)

    true

    (af/f= (af/d-inv af/mobius) af/one)

    true

Function `d-inv` defined as recursive function, it may
execute slow. But inverse of completely multiplicative function $f(n)$
is $f(n) \mu(n)$(usual multiplication), for instance inverse
of identity function, let's denote it $N(n)$ is $N(n) \mu(n)$

    (af/f=
     (af/d-* 
        #(* (identity %) (af/mobius %))
        identity
     )
     af/unit)

    true


<a id="org0e795f2"></a>

# Conguences `vk.ntheory.congruence`

This section cover namespaces `vk.ntheory.congruence`. It contains functions
for solve linear congruence and system of linear congruences.

    (require '[vk.ntheory.congruence :as c])

In progress &#x2026;


<a id="org89f96eb"></a>

# Primitive Roots `vk.ntheory.primitive-roots`

    (require '[vk.ntheory.primitive-roots :as pr])

In progress&#x2026;


<a id="orga891dc7"></a>

# Quadratic residies ~vk.ntheory.quadratic-residue

    (require '[vk.ntheory.quadratic-residue :as qr])

In progress&#x2026;

