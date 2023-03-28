# RSE Project

## Introduction

This is the starting point for the RSE project.

## Changes

In case of changes to the project description, we will communicate them via
[this Moodle
thread](https://moodle-app2.let.ethz.ch/mod/forum/discuss.php?d=123670). Please
check both this thread and also other threads on Moodle regularly.

## Project Description

### Motivation

Imagine you are in charge of the deliveries for a chain of grocery stores. You will organize deliveries of products to stores, where the delivery will be moved with a trolley to the reserve where it is stored.
As you write up a program to record planned deliveries, you want to make sure that there are no errors: a delivery should always deliver a positive amount of product, should not be bigger than the trolley size of the store, and the total amount received by a store should not be bigger than the size of its reserve. To
verify your tentative programs, you decide to apply program analysis as taught
in RSE.

### Description

Consider the following class `Store`:

```java
/**
 * We are verifying calls into this class
 * 
 */
public final class Store {

  // Trolley and reserve size for the store
  private final int trolley_size, reserve_size;
  // Amount of product received so far
  int received_amount;

  public Store(int trolley_size, int reserve_size) {
    this.trolley_size = trolley_size;
    this.reserve_size = reserve_size;
    this.received_amount = 0;
  }

  public void get_delivery(int volume) {
     // check NON_NEGATIVE
    assert 0 <= volume;

    // check FITS_IN_TROLLEY
    assert volume <= this.trolley_size;
    this.received_amount += volume;

    // check FITS_IN_RESERVE
    assert this.received_amount <= this.reserve_size;
  }
}
```

The goal of the project is to implement a program analyzer that takes as input a
Java program which makes use of the `Store` class. The program analyzer then
verifies that the following conditions hold for this program:

Property NON_NEGATIVE:

- For any **reachable** invocation of `get_delivery(v)` on an object `o` of class
  `Store`, `v >= 0`.

Property FITS_IN_TROLLEY:

- For any **reachable** invocation of `get_delivery(v)` on an object `o` of class
  `Store`, `v <= o.trolley_size`.

Property FITS_IN_RESERVE:

- For any **reachable** invocation of `get_delivery(v)` on an object `o` of class
  `Store`, after the call is executed we have that `o.received_amount <= o.reserve_size`.

These properties should be viewed as independent: for example, `FITS_IN_TROLLEY`
may be `SAFE` even if `NON_NEGATIVE` is `UNSAFE`. In particular, you may
assume that only one assertion is checked at a time, and that violations of
other assertions are ignored.

Your program analyzer (see skeleton below) must take as input a `TEST_CLASS` and
a `VerificationProperty` (e.g., `NON_NEGATIVE`), and return true (called `SAFE`)
if the property is guaranteed to hold, and false (called `UNSAFE`) if the
property cannot be proven.

## Example 1 (SAFE)

```java
// expected results:
// NON_NEGATIVE SAFE
// FITS_IN_TROLLEY SAFE
// FITS_IN_RESERVE SAFE

public class Basic_Test_Safe {

  public static void m1() {
    Store s = new Store(2, 4);
    s.get_delivery(2);
    s.get_delivery(1);

    Store s2 = new Store(4, 4);
    s2.get_delivery(4);
  }
}
```

## Example 2 (UNSAFE)

```java
// expected results:
// NON_NEGATIVE UNSAFE
// FITS_IN_TROLLEY UNSAFE
// FITS_IN_RESERVE UNSAFE

public class Basic_Test_Unsafe {

  public void m2(int j) {
    Store s = new Store(1, 2);
    if(-1 <= j && j <= 3)
      s.get_delivery(j);
  }
}
```

## Project Repository

For each group, we have set up a repository with a skeleton for your solution,
which you should see at
[https://gitlab.inf.ethz.ch/dashboard/projects](https://gitlab.inf.ethz.ch/dashboard/projects).
After reading this project description, you should read and follow the
[README.md](/README.md) file. It contains instructions on how to set up the
project and run it.

The output of the skeleton is initially always `SAFE`, which is unsound. The
goal of the project is to follow the comments in the code (check for `FILL THIS
OUT`), such that the project only reports `SAFE` for test classes where we can
**guarantee** it (but as often as possible). Feel free to change any part of the
skeleton (even parts not marked with `FILL THIS OUT`), and to create new files,
as long as you do not change files which tell you `DO NOT MODIFY THIS FILE`.

## Libraries

For your analysis, you will use the two libraries APRON and Soot. Part of your
assignment is understanding these libraries sufficiently to leverage them for
program analysis. In addition to the resources provided below, you may also
consult

- The course lectures on abstract interpretation and pointer analysis.
- The language fragment of Soot to handle (see below).
- The documentation for APRON and Soot (including documentation of methods and
  classes), which is available in Visual Studio Code after setting up the
  project (see [README.md](/README.md#development-optional-but-recommended) file).

### APRON

[APRON](http://apron.cri.ensmp.fr/library/) is a library for numerical abstract
domains. An example file of using APRON exists
[here](/analysis/src/test/java/apron/ApronTest.java) - it should demonstrate
everything you need to know about APRON.

If you require more resources (which should not be necessary), you can also find
documentation about the APRON framework [here](./apron-doc/index.html), and more
extensive usage examples
[here](https://github.com/antoinemine/apron/blob/cf9017f99655e514b1ba336a5c56c548189ccd64/japron/apron/Test.java).

### Soot

Your program analyzer is built using [Soot](https://github.com/soot-oss/soot), a
framework for analyzing Java programs. You can learn more about Soot by reading
its
[tutorial](http://www-labs.iro.umontreal.ca/~dufour/cours/ift6315/docs/soot-tutorial.pdf),
[survivor guide](https://www.brics.dk/SootGuide/sootsurvivorsguide.pdf), and
[javadoc](https://www.sable.mcgill.ca/soot/doc/index.html). If you require more
resources (which should not be necessary), you can find additional tutorials
[here](https://github.com/Sable/soot/wiki/Tutorials).

Your program analyzer will use Soot's pointer analysis to determine which
variables may point to `Store` objects (see the
[Verifier.java](/analysis/src/main/java/ch/ethz/rse/verify/Verifier.java) file
in your skeleton).

### Language Fragment to Handle

For this project, you will analyse a fragment of Jimple. This language contains
only local integer variables and `Store` objects. Note that the type of
integer variables can be int, byte, short, or bool (e.g., `int i = 10;` is
represented as byte, see also
[SootHelper.java](../../analysis/src/main/java/soot/SootHelper.java) ->
`isIntValue`).

- Details about the Jimple language can be found
  [here](https://www.sable.mcgill.ca/soot/doc/index.html)
- The language fragment to handle is:

| Jimple Construct                                                                       | Meaning                                                                                                                                                                                                                                                    |
|----------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [DefinitionStmt](https://www.sable.mcgill.ca/soot/doc/soot/jimple/DefinitionStmt.html) | Definition Statement: here, you only need to handle integer assignments to a local variable. That is, `x = y`, or `x = 5` or `x = EXPR`, where `EXPR` is one of the three binary expressions below. That is, you need to be able to handle: `y = x + 5` or `y = x * z`. |
| [JMulExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JMulExpr.html)    | Multiplication                                                                                                                                                                                                                                             |
| [JSubExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JSubExpr.html)    | Subtraction                                                                                                                                                                                                                                                |
| [JAddExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JAddExpr.html)    | Addition                                                                                                                                                                                                                                                   |
| [JIfStmt](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JIfStmt.html)      | Conditional Statement. You need to handle conditionals where the condition can be any of the binary boolean expressions below. These conditions can again only mention integer local variables or constants, for example: `if (x > y)` or `if (x <= 4)`, etc.  |
| [JEqExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JEqExpr.html)      | ==                                                                                                                                                                                                                                                         |
| [JGeExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JGeExpr.html)      | >=                                                                                                                                                                                                                                                         |
| [JGtExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JGtExpr.html)      | >                                                                                                                                                                                                                                                          |
| [JLeExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JLeExpr.html)      | <=                                                                                                                                                                                                                                                         |
| [JLtExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JLtExpr.html)      | <                                                                                                                                                                                                                                                          |
| [JNeExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JNeExpr.html)      | != |
| [IntConstant](https://www.sable.mcgill.ca/soot/doc/soot/jimple/IntConstant.html)      | Integer constant |
| [JimpleLocal](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JimpleLocal.html)      | Local variable |
| [ParameterRef](https://www.sable.mcgill.ca/soot/doc/soot/jimple/ParameterRef.html)      | Method parameter |
| [JInvokeStmt](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JInvokeStmt.html)      | Call to `get_delivery` (cp. [JVirtualInvokeExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JVirtualInvokeExpr.html)) or initializer for new `Store` object (cp. [JSpecialInvokeExpr](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JSpecialInvokeExpr.html)) |
| [JReturnVoidStmt](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JReturnVoidStmt.html)      | Return from function |
| [JGotoStmt](https://www.sable.mcgill.ca/soot/doc/soot/jimple/internal/JGotoStmt.html)      | Goto statement |

- Loops are also allowed in the programs.
- Assignments of pointers of type `Store` are possible, e.g. `e = f` where `e`
  and `f` are of type `Store`. However, those are handled by the pointer
  analysis.

## Implementation tips

- You may assume the class you analyze only has a single method in addition to
  its constructor (called `<init>` in Soot). You may assume that the constructor
  is empty.
- You can assume the constructor `Store` takes as arguments (`trolley_size`, `reserve_size`) only **integer constants** (never local variables). However, the method `get_delivery` can be called with any element of the language fragment detailed above.
- You can assume all analyzed methods only have integer parameters (in
  particular, they cannot have `Store` parameters).
- The analyzed code may contain loops and branches.
- You will need to apply widening. Do this after `WIDENING_THRESHOLD` steps (see
  [NumericalAnalysis.java ->
  WIDENING_THRESHOLD](../../analysis/src/main/java/ch/ethz/rse/numerical/NumericalAnalysis.java)).
  Our grading will ensure that increasing `WIDENING_THRESHOLD` does not increase
  your score.
- Only local variables need to be tracked for the numerical analysis (no global
  variables), but for the heap you need to use the existing pointer analysis of
  Soot. The skeleton already contains the invocation of the pointer analysis.
  You can then leverage the result of this pointer analysis for your numerical
  analysis.
- You can assume the analyzed code never throws exceptions, such as `null`
  dereferences, or division by zero.
- You may ignore overflows in your implementation (in other words, you may
  assume that APRON captures Java semantics correctly)
- It is enough to use the polyhedra domain that
  [APRON](http://apron.cri.ensmp.fr/library/) provides (Polka) to analyze
  relations over the local integer variables.
- If you see an operation for which you are not precise - do not crash, but be
  less precise or go to top instead so that you remain sound. This is useful in
  case of misunderstandings on the project description.
- The three properties to check are ordered by difficulty. We recommend you work
  on them in the order NON_NEGATIVE, FITS_IN_TROLLEY, FITS_IN_RESERVE. The last
  property is meant as a challenge for stronger groups (but solving it is
  necessary for full points).
- We strongly recommend you to test your implementation on many examples (you
  should come up with your own examples).

## Deliverables

- The project deadline is **Wednesday, June 7th, 3, 17:00** (Zurich time)!
- We may decline to answer project questions after Monday, June 5th, 2023,
  17:00. This avoids last-minute revelations that cannot be incorporated by all
  groups.
- Commit and push your project to the master branch of your
  [GitLab](https://gitlab.inf.ethz.ch/) repository (that originally contained
  the skeleton) before the project deadline. **Please do not update your
  repository (commit, revert, etc.) after the deadline** - we will flag groups
  that try this.
- If you cannot access your GitLab repository, contact
  <anouk.paradis@inf.ethz.ch>.

## Grading

- We will evaluate your tool **on our own set of programs** for which we know if
  they are valid or not.
- Your project must use the setup of the provided skeleton. In particular, you
  cannot use libraries other than those provided in
  [analysis/pom.xml](/analysis/pom.xml).
- We will evaluate you depending on the correctness of your program and the
  precision of your solution. You will not get points if your program does not
  satisfy the requirements. If your solution is unsound (i.e. says that an
  unsafe code is safe), or imprecise (i.e. says that a safe code is unsafe) we
  will penalize it.
- We will **penalize unsoundness much more** than imprecision.
- There will be a time limit of 10 seconds and 1GB of RAM to verify an
  application. Use [this script](/analysis/run.sh) if you want to ensure your
  solution adheres to these limits. Because the performance of a given solution
  is sometimes hard to predict, this limit is chosen generously.
- We will award **additional points** for groups that achieve a test instruction
  coverage of `>=75%` (achieving coverage `>75%` does not yield more points than
  achieving `75%`). If some of your tests fail, we will not award any additional points.
- Your solution must use abstract interpretation. Do not use other techniques
  like symbolic execution, testing, brute force, random guessing, machine
  learning, etc.
- Do not try to cheat (e.g., by reading the solutions from the test file)!
- Only submit solutions that **you have written yourself** and that you have
  understood. Cross-team implementation and copy paste (except from the project
  skeleton itself) is not permitted.

### Master solution

In case you are unsure if you are expected to be precise on a given program, you
may query the master solution [here](http://rseproject.ethz.ch/rse-project) to
gauge the precision we expect from a top-graded project (you need to provide
your nethz username and password).

## Project Assistance

For questions about the project, please consult, **in this order**:

- This project description
- The skeleton in your GitLab repository (in particular the [README](/README.md)
  file)
- The documentation of libraries&frameworks, in particular APRON and Soot
  discussed above
- The [Moodle
  page](https://moodle-app2.let.ethz.ch/mod/forum/view.php?id=857208). All
  students will see and are encouraged to reply to the questions about the
  project.
- The project TA at <anouk.paradis@inf.ethz.ch> (only when Moodle is not possible)
