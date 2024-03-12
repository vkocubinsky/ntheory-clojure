
# Table of Contents

1.  [Arithmetical functions](#org0ae762b)
    1.  [Function equality](#org14645f4)
    2.  [Additive functions](#org4a611c4)
    3.  [Multiplicative functions](#org4081116)
    4.  [Higher order function for define multiplicative and additive functions](#org6e23bfd)
    5.  [Some additive functions](#org58df5ff)
        1.  [Count of distinct primes - $\omega$](#org8ec1feb)
        2.  [Total count of primes - $\Omega$](#orge327a41)
    6.  [Some multiplicative functions](#org0f22a4e)
        1.  [Mobius function - $\mu$.](#orgd65f498)
        2.  [Euler totient function - $\phi$](#orge41584a)
        3.  [Unit function - $\epsilon$](#orge290ca3)
        4.  [Constant one function - $1$](#orgfc0ebc8)
        5.  [Divisors count - $\sigma_0$](#orgb6a99ba)
        6.  [Divisors sum - $\sigma_1$](#org4a14b02)
        7.  [Divisors square sum](#org114265f)
        8.  [Divisors higher order function - $\sigma_{x}$](#org89f310c)
        9.  [Liouville - $\lambda$](#orgaec5b77)
    7.  [Some other arithmetic functions](#orgf7cec84)
        1.  [Mangoldt - $\Lambda$](#orgc9743cf)
        2.  [Chebyshev functions $\theta$ and $\psi$](#org9f27978)
    8.  [Dirichlet convolution](#orgd9a6dc8)


<a id="org0ae762b"></a>

# Arithmetical functions

Arithmetical function is an any function which accept natural number
and return complex number $f: \mathbf N \to \mathbf C$. The library mostly works
with functions which also returns integer $f: \mathbf N \to \mathbf Z$.


<a id="org14645f4"></a>

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
    (nt/f-equals f g)
    (nt/f-equals f g (range 1 1000))
    (nt/f-equals f g (filter even? (range 1 100)))


<a id="org4a611c4"></a>

## Additive functions

Additive function is a function for which

$$ f(mn) = f(m) + f(n)$$

if $m$ relatively prime to $n$. If above equality holds for all
natural $m$ and $n$ function called completely additive.

To define an additive function it is enough to define how to
calculate a function on power of primes.
If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then: 

$$ f(n) = \sum_{i=1}^{k} f({p_i}^{a_i}) $$


<a id="org4081116"></a>

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


<a id="org6e23bfd"></a>

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
    (nt/reduce-on-prime-count * (fn [p k] (inc k))))

    (my-divisors-count 6)

    class clojure.lang.Compiler$CompilerException

Of course there is predefined function `divisors-count`, but it
is an example how to define custom function.


<a id="org58df5ff"></a>

## Some additive functions


<a id="org8ec1feb"></a>

### Count of distinct primes - $\omega$

Count of distinct primes is a number of distinct primes which
divides given $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then $\omega = k$.

    (nt/primes-count-distinct (* 2 2 3))

    class clojure.lang.Compiler$CompilerException


<a id="orge327a41"></a>

### Total count of primes - $\Omega$

Total count of primes is a number of primes and power of primes
which divides $n$. If $n = p_1^{a_1} p_2^{a_2} \dots p_k^{a_k}$ then:

$$\Omega = a_1 + a_2 + \dots + a_k$$

    (nt/primes-count-total (* 2 2 3))

    class clojure.lang.Compiler$CompilerException


<a id="org0f22a4e"></a>

## Some multiplicative functions


<a id="orgd65f498"></a>

### Mobius function - $\mu$.

Mobius function defined as:

$$ \mu(n) = \begin{cases}
1        &  \quad \text{if } n = 1 \\
(-1)^k   &  \quad \text{if } n \text{ product of distinct primes} \\
0        &  \quad \text{otherwise}
\end{cases} $$

For example, $\mu(6)=\mu(2 \cdot 3)=1$

    (nt/mobius 6)

    class clojure.lang.Compiler$CompilerException


<a id="orge41584a"></a>

### Euler totient function - $\phi$

Euler totient function  is a count of numbers relative  prime to given
number `n`.  Totient function can be calculated by formula:

$$ \phi(n) = \prod_{p|n} (p^a - p^{a-1}) $$

For example, count of numbers relative prime to $6$ are $1$ and $5$, so $\phi(6) = 2$

    (nt/totient 6)

    class clojure.lang.Compiler$CompilerException


<a id="orge290ca3"></a>

### Unit function - $\epsilon$

Unit function defined as

$$ \epsilon(n) = \begin{cases}
1,&  \text{if } n = 1 \\
0,&  \text{if } n > 1
\end{cases} $$

    (nt/unit 6)

    class clojure.lang.Compiler$CompilerException


<a id="orgfc0ebc8"></a>

### Constant one function - $1$

$$ 1(n) = 1 $$

    (nt/one 6)

    class clojure.lang.Compiler$CompilerException


<a id="orgb6a99ba"></a>

### Divisors count - $\sigma_0$

Divisors count is number of divisors which divides given number $n$.

$$ \sigma_0(n) = \sum_{d|n} 1 $$

For example, number $64$ has $4$ divisors, namely $1,2,3,6$, so $\sigma_0(6)=4$

    (nt/divisors-count 6)

    class clojure.lang.Compiler$CompilerException


<a id="org4a14b02"></a>

### Divisors sum - $\sigma_1$

$$ \sigma_1(n) = \sum_{d | n} d $$

For number 6 it is $12 = 1 + 2 + 3 + 6$

    (nt/divisors-sum 6)

    class clojure.lang.Compiler$CompilerException


<a id="org114265f"></a>

### Divisors square sum

$$ \sigma_2(n) = \sum_{d | n} d^2 $$

For number 6 it is $50 = 1^2 + 2^2 + 3^2 + 6^2$

    (nt/divisors-square-sum 6)

    class clojure.lang.Compiler$CompilerException


<a id="org89f310c"></a>

### Divisors higher order function - $\sigma_{x}$

In general $\sigma_x$ function is a sum of x-th powers divisors of given n

$$ \sigma_x(n) = \sum_{ d | n} d^x $$

If $x \ne 0$ $\sigma_x$ can be calculated by formula:

$$ \sigma_{x}(n) = \prod_{i=1}^{k} \frac {p_i^{(a_i+1)x}} {p_i^x - 1} $$

and if $x = 0$ by formula:

$$ \sigma_{0}(n) = \prod_{i=1}^{k} (a_i + 1) $$

There is higher order function `divisors-sum-x` which
accept `x` and return appropriate function.

    (def my-divisors-square-sum (nt/divisors-sum-x 2))


<a id="orgaec5b77"></a>

### Liouville - $\lambda$

Liouville function can be defind by formula:

$$\lambda(n) = (-1)^{\Omega(n)}$$

where [$\Omega$](#orge327a41) have been descibed above.

    (nt/liouville (* 2 3)) 

    class clojure.lang.Compiler$CompilerException


<a id="orgf7cec84"></a>

## Some other arithmetic functions


<a id="orgc9743cf"></a>

### Mangoldt - $\Lambda$

$$\Lambda(n) = \begin{cases}
   \log p,& \text{if $n$ is power of prime i.e. $n = p^k$} \\
   0,& \text{otherwise} 
\end{cases}$$

For example $\Lambda(8) = \log 2$, $\Lambda(6) = 0$  

    (nt/mangoldt 2)
    (nt/mangoldt 6)

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-left" />
</colgroup>
<tbody>
<tr>
<td class="org-left">class clojure.lang.Compiler$CompilerException</td>
</tr>


<tr>
<td class="org-left">class clojure.lang.Compiler$CompilerException</td>
</tr>
</tbody>
</table>


<a id="org9f27978"></a>

### Chebyshev functions $\theta$ and $\psi$

There are two Chebyshev functions, one $\theta$ is defined as

$$\theta(x) = \sum_{p \le x} \log p$$

second $\psi$ defined as

$$\psi = \sum_{n \le x} {\Lambda(n)} $$

where [$\Lambda$](#orgc9743cf) have been described above

    (nt/chebyshev-first 2)
    (nt/chebyshev-second 2)

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-left" />
</colgroup>
<tbody>
<tr>
<td class="org-left">class clojure.lang.Compiler$CompilerException</td>
</tr>


<tr>
<td class="org-left">class clojure.lang.Compiler$CompilerException</td>
</tr>
</tbody>
</table>


<a id="orgd9a6dc8"></a>

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

    (nt/f-equals
       (nt/dirichlet-convolution nt/one nt/one)
       nt/divisors-count
    )

    class clojure.lang.Compiler$CompilerException

Dirichlet convolution is associative so clojure method support more than two
function as parameter of `f*`

    (nt/f-equals
      (nt/dirichlet-convolution nt/mobius nt/one nt/mobius nt/one)
      nt/unit
    )

    class clojure.lang.Compiler$CompilerException

Another example, functions $\mu(n)$ and $1(n)$ are inverse of each other

    (nt/f-equals (nt/dirichlet-inverse nt/one) nt/mobius)
    (nt/f-equals (nt/dirichlet-inverse nt/mobius) nt/one)

<table border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">


<colgroup>
<col  class="org-left" />
</colgroup>
<tbody>
<tr>
<td class="org-left">class clojure.lang.Compiler$CompilerException</td>
</tr>


<tr>
<td class="org-left">class clojure.lang.Compiler$CompilerException</td>
</tr>
</tbody>
</table>

