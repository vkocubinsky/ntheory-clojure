# About

Number theory with Clojure. This project cover some topics in number
theory, especially arithmetic functions, further more multiplicative
functions. There are set of well known multiplicative functions and one
can define custom multiplicative functions.

I wrote this document with Emacs org mode. Then I generated markdown
file to show it nicely on github. I use Emacs babel to produce real
output inside the document.

In this document I load number theory package as:

``` clojure
(require '[vk.ntheory :as nt])
```

So below I will use `nt` alias.

# Prime numbers

There is `sieve` functions which returns prime numbers which not exceeds
given `n`.

``` clojure
(nt/sieve 30)
```

``` example
#{2 3 5 7 11 13 17 19 23 29}

```

Prime numbers which returns by `sieve` function cached, and then in next
time if one call `sieve` function again, real calculation only happened
when new `n` more than cached upper limit, otherwise cached prime
numbers is used to get the result.

Cache is stored in `sieve-table` atom.

``` clojure
@nt/sieve-table
```

``` example
{:table #{2 3 5 7 11 13 17 19 23 29}, :upper 30}

```

To reset cache just call

``` clojure
(nt/reset-sieve-table!)
```

# Integer factorization

Every integer more than $1$ can be represented uniquely as a product of
primes.

$$
n = {p_1}^{a_1} {p_2}^{a_2} \dots {p_k}^{a_k} = \prod_{i=1}^{k} {p_i}^{a_i}
$$

If we accept that empty product is $1$ we can say that every natural
numbers can be represent uniquely as a product of primes. For example
$360 = 2^3 3^2 5^1$.

There is a function `factorize` which return factorization for given
`n`. The result is a sorted map for which key is a prime number and
value is an order of prime in `n`.

``` clojure
(nt/factorize 360)
```

``` example
{2 3, 3 2, 5 1}

```

There is also inverse function of `factorize` which accept prime
factorization and return integer.

``` clojure
(nt/de-factorize {2 3, 3 2, 5 1})
```

``` example
360

```

Implementation of `factorize` function use table of primes calculated by
`sieve` function. To factorize number `n` it is enough to calculate
prime numbers less or equals to $\sqrt n$. So keep table for primes less
or equal to 1000 enough to factorize any number less or equal to
1000000.

# Divisors

For get list of all divisors of number `n` there is `divisor` function.
List of divisors is unordered.

``` clojure
(nt/divisors 30)
```

``` example
(1 2 3 6 5 10 15 30)

```

# Arithmetical functions

Arithmetical function is an any function which accept natural number. I
mainly works which functions which also returns integer.

# Function equality

Two arithmetical function $f$ and $g$ are equal if $f(n)=g(n)$ for all
natual $n$. There is helper function `f-equals` which compare two
functions on some subset of natual numbers. Function `f-equals` accept
two functions and subset of natural numbers. There is a default for
subset of natural numbers, currently it is `range(1,100)`.

If we like identify does two function `f` and `g` equals on some subset
of natural number we can for example do next:

``` clojure
(nt/f-equals f g)
(nt/f-equals f g (range 1 1000))
(nt/f-equals f g (filter even? (range 1 100)))
```

# Multiplicative functions

Important class of arithmetical functions consists multiplicative
functions. Multiplicative function is a function for which $f(1)=1$ and

$$ f(mn) = f(m)f(n) \quad \text{if } m \text{ relatively prime to } n $$

To define multiplicative function it is enough to define how to
calculate a function on power of primes.

$$ f(n) = \prod_{i=1}^{k} f({p_i}^{a_i}) $$

There is higher order functions `multiplicative-function` which accept
function to calculate multiplicative function on power of primes and
return function defined for all natural numbers.

For instance, we can define function which calculate number of divisors
of integer `n`. Count of divisors of number `n` can be calculated by
formula

$$ \sigma_0(n) = \prod_{i=1}^{k} (a_i + 1) $$

``` clojure
(def my-divisors-count
(nt/multiplicative-function (fn [p k] (inc k))))
```

``` clojure
(my-divisors-count 6)
```

``` example
4

```

Of course there is predefined function `disvisors-count`, but it is an
example how to define custom function.

# Predefined functions

## Mobius function - $\mu$.

Mobius function defined as:

$$ f(n) = \begin{cases}
1        &  \quad \text{if } n = 1 \\
(-1)^k   &  \quad \text{if } n \text{ product of distinct primes} \\
0        &  \quad \text{otherwise}
\end{cases} $$

For example, $\mu(6)=\mu(2 \cdot 3)=1$

``` clojure
(nt/mobius 6)
```

``` example
1
```

## Euler totient function - $\phi$

Euler totient function is a count of numbers relative prime to given
number `n`. Totient can be calculated by formula:

$$ \phi = \prod_{i=1}^k (p^k - p^{k-1}) $$

For example, count of numbers relative prime to $6$ are $1$ and $5$, so
$\phi(6) = 2$

``` clojure
(nt/totient 6)
```

``` example
2
```

## Unit function - $\epsilon$

Unit function defined as

$$ f(n) = \begin{cases}
1,&  \text{if } n = 1 \\
0,&  \text{if } n = 0
\end{cases} $$

``` clojure
(nt/unit 6)
```

``` example
0

```

## Constant one function - $1$

$$ f(n) = 1 $$

``` clojure
(nt/one 6)
```

``` example
1

```

## Divisors count - $\sigma_0$

Divisors count is number of divisors which divides given number $n$.

$$ \sigma_0(n) = \sum_{d|n} 1 $$

For example, number \$64 has $4$ divisors, namely $1,2,3,6$, so
$\sigma_0(6)=4$

``` clojure
(nt/divisors-count 6)
```

## Divisors sum - $\sigma_1$

$$ \sigma_1(n) = \sum_{d | n} d $$

For number 6 it is $12 = 1 + 2 + 3 + 6$

``` clojure
(nt/divisors-sum 6)
```

``` example
12

```

## Divisors square sum

$$ \sigma_2(n) = \sum_{d | n} d^2 $$

For number 6 it is $50 = 1^2 + 2^2 + 3^2 + 6^2$

``` clojure
(nt/divisors-square-sum 6)
```

``` example
50

```

## Divisor higher order function - $\sigma_{x}$

In general $\sigma_x$ function is a sum of x-th powers divisors of given
n

$$ \sigma_x(n) = \sum_{ d | n} d^x $$

If $x \ne 0$ $\sigma_x$ can be calculated by formula:

$$ \sigma_{x}(n) = \prod_{i=1}^{k} \frac {p_i^{(a_i+1)x}} {p_i^x - 1} $$

and if $x = 0$ by formula:

$$ \sigma_{0}(n) = \prod_{i=1}^{k} (a_i + 1) $$

There is higher order function `divisors-sum-x` which accept `x` and
return appropriate function.

``` clojure
(def my-divisors-square-sum (nt/divisors-sum-x 2))
```

# Dirichlet convolution

For two arithmetic functions $f$ and $g$ Dirichlet convolution is a new
arithmetic function defined as

$$ (f*g)(n) = \sum_{d | n} f(d)g(\frac{n}{d}) $$

Dirichlet convolution is associative

$$ (f * g) * h = f * (g * h) $$

Commutative

$$ f * g = g * f $$

Has identify

$$ f * \epsilon = \epsilon * f = f $$

For every $f$, which $f(1) \ne 0$ exists inverse function $f^{-1}$ such
that $f * f^{-1} = \epsilon$. This inverse function called Dirichlet
inverse and can by calculated recursively by:

$$ f^{-1}(n) = \begin{cases}
\frac{1}{f(1)} & \quad \text{if } n = 1  \\
\frac{-1}{f(1)}\sum_{ \substack{d | n\\
                                d < n}} f(\frac{n}{d}) f^{-1}(d)
               & \quad n \ge 1
\end{cases} $$

For example, $1(n) * 1(n) = \sigma_0$

``` clojure
(nt/f-equals
   (nt/dirichlet-convolution nt/one nt/one)
   nt/divisors-count
)
```

``` example
true

```

Dirichlet convolution is associative so clojure method support more than
two function as parameter of `dirichlet-convolution`

``` clojure
(nt/f-equals
  (nt/dirichlet-convolution nt/mobius nt/one nt/mobius nt/one)
  nt/unit
)
```

``` example
true

```

Another example, functions $\mu(n)$ and $1(n)$ are inverse of each other

``` clojure
(nt/f-equals (nt/dirichlet-inverse nt/one) nt/mobius)
(nt/f-equals (nt/dirichlet-inverse nt/mobius) nt/one)
```

|      |
|------|
| true |
| true |
