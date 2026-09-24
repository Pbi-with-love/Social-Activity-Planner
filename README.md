# Project context: Social Activity Planner (Bachelor thesis)

## Idea
A platform that helps a group decide what to do together. Members submit individual constraints and preferences; the system generates a ranked, explainable group activity plan. Once a plan is accepted it becomes an activity with a chat and an optional opt-in live map. The planning engine is the academic core; everything else is supporting product and must not grow into a Facebook clone.

## Problem
Friends with different budgets, schedules, travel limits and interests struggle to agree on one practical plan. A human organizer has to collect inputs, resolve conflicts and pick a plan.

## Thesis focus
Group activity planning under heterogeneous preferences and constraints.
- RQ1: How should individual preferences be represented so they can be combined at group level?
- RQ2: How can the planner resolve member conflicts while respecting hard constraints?
- RQ3: How does the approach perform as group size and preference conflict increase?
Contribution: a formal model of hard constraints and soft preferences, a planner that generates and ranks candidates, a measurable fairness mechanism, an experimental comparison against baselines, and a working prototype.

## Planning method
1. Hard constraints (must hold): day, max budget, duration range, max travel distance. Invalid candidates are filtered out first.
2. Soft preferences: weights 1-5 per category or tag. memberScore(member, activity) = sum of weights of satisfied preferences.
3. Group score = W_total * average(memberScore) + W_fair * min(memberScore) - W_cost * normalizedCost - W_time * normalizedTravelTime. Weights are configurable parameters, not universal truths.
4. Rank the valid candidates and return the top 3, each with an explanation (why it scored well, or which constraint rejected it).
5. Deterministic: the same input must always give the same ranking.
The planner is a pure Java service behind a PlanningStrategy interface, independent of HTTP and database code, so multiple strategies can be compared.

## Strategies to compare (baselines)
Majority voting, average score, least misery (maximin), and the fairness-aware weighted planner. Optional extension: an ILP/CP-SAT strategy. The goal is to measure which objective each method achieves under the same inputs, not to claim one is universally best.

## MVP scope (modular monolith)
Auth, profile with tags, group creation and joining, preference form, plan generation, plan acceptance into an activity, simple real-time activity chat. Smart Map (opt-in, activity-scoped, TTL-limited live location) is optional and comes last.
Stack: Java + Spring Boot, PostgreSQL, Redis (latest location with TTL), WebSocket, React Native + Expo.
Out of MVP: feed and followers, likes, background tracking, payments, multi-stop itineraries.

## Evaluation
Metrics: preference satisfaction, fairness (minimum satisfaction and spread), hard-constraint violations, planning latency, explainability. Synthetic scenarios: 3/5/10/30/100 members with low/medium/high conflict. Also a small user study (10-20 people) comparing the planner's plan against the group's own decision. Log the exact input JSON, removed candidates, per-member contributions, score components and execution time for every test case.
