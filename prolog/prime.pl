% Student exercise profile
:- set_prolog_flag(occurs_check, error).        % disallow cyclic terms
:- set_prolog_stack(global, limit(80 000 000)).  % limit term space (8Mb)
:- set_prolog_stack(local,  limit(80 000 000)).  % limit environment space
:- dynamic counter/2.
:- dynamic primes/2.

% Your program goes here
prime_test(A,B,C,P) :-
    assert(counter(1,0)),
    assert(primes(1,0)),
    construct_list(A,B,L),
    prime_from_list(L),
    counter(1,C),
    primes(1,P).

construct_list(A,B,_) :-
    A > B,!,false.
construct_list(A,B,L) :-
    construct_list_int(A,B,[],B, L).

construct_list_int(A,_,L,C,L):-
    C < A, !.
construct_list_int(A,B,T,C,L) :-
    C1 is C-2,
    construct_list_int(A,B,[C|T],C1,L).

prime_from_list([]) :-!.
prime_from_list([H|T]):-
    is_prime(H),!,
    primes(1,C),
    retract(primes(1,C)),
    C1 is C + 1,
    assert(primes(1,C1)),
    prime_from_list(T).
prime_from_list([_|T]):-
    prime_from_list(T).

is_prime(2) :- !.
is_prime(3) :- !.
is_prime(X) :-
    X > 3,
    R is X mod 2,
    \+(R == 0),
    Nmax is floor(sqrt(X)),
    is_prime_int(3, Nmax, X).

is_prime_int(N, Nmax, _) :-
    N > Nmax,!.
is_prime_int(N, Nmax, X) :-
    R is X mod N,
    counter(1,C),
    retract(counter(1,C)),
    C1 is C + 1,
    assert(counter(1,C1)),
    \+(R == 0),
    N2 is N + 2,
    is_prime_int(N2, Nmax, X).

