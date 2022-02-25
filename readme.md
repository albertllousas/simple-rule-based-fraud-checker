# Simple ruled-based fraud checker

A simple and domain focussed rule-based fraud checker.

## Motivation

Almost all rule-based systems are crazy complex, they are built using rule-engine libraries or DSLs to achieve levels of 
abstraction that usually requires a lot of knowledge to maintain, add new rules or just understand the flows, because most 
of the time, they are just totally over-engineered. 

But, when we start a project, more likely we will have:
- A lot of ambition but a small set of business rules to apply
- An uncertainty about how and when our rules are going to grow

Therefore, do we need to be coupled with a complex code-base? 

No, we want rules that can be easily and quickly adapted to our business necessities, not the business being adapted to our rule-engine.

This project tries to do the opposite of these rule-engines commented above, it tries to build a system without too much abstraction, 
focussed in the problem to solve, built around the domain, a system easy to understand and evolve.

Principles and practices applied:
- YAGNI
- Clean code (easy to understand and change)
- Domain-Driven Design
- Hexagonal Architecture
- Outside-in TDD
- Framework-agnostic: Everything has been done in plain Kotlin

Even though the example is about fraud check, it can easily extrapolated to any other domain with a rule-based problem
to solve.

## Description

<p align="center">
  <img width="90%" src="fraud-checker.png">
</p>

## How to abstract if it grows


## To consider if used in production

- Add a framework to run it, without it the project is pretty useless :-)
- All the adapters are in memory implementations, just for the sake of the demo, please don't use it in real environments, 
  substitute them by real stream platforms and real data-stores. 
- Consider transactional outbox pattern to deal with dual-writes
- Decouple login storing and use-case triggering (check suspicious login), now it's sync. It can be easily achieved 
  publishing an event to a private stream after storing the login and subscribe to it oin order to trigger the use-case.


