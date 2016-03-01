package gustafc.session02;

import fj.Ord;
import fj.data.List;
import fj.data.Option;
import fj.data.TreeMap;
import rx.Observable;

import java.util.Optional;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static fj.data.List.arrayList;

public class Calculator {

    public static Observable<String> run(Observable<String> commands) {
        return commands
                .map(Command::parse)
                .scan(State.INITIAL, State::progress)
                .flatMap(state -> Observable.from(state.updatedFormulae()));
    }

    private static final class State {
        static final State INITIAL = new State(TreeMap.empty(Ord.stringOrd), List.nil(), List.nil());

        final TreeMap<String, Double> variables;
        final List<Formula> formulae;
        final List<String> updated;


        State(TreeMap<String, Double> variables, List<Formula> formulae, List<String> updated) {
            this.variables = variables;
            this.formulae = formulae;
            this.updated = updated;
        }

        State progress(Command cmd) {
            return cmd.receive(
                    assignment -> new State(variables.set(assignment.name, assignment.value), formulae, arrayList(assignment.name)),
                    formula -> new State(variables, formulae.cons(formula), formula.variables())
            );
        }

        public Iterable<String> updatedFormulae() {
            return formulae.filter(f -> f.variables().exists(var -> updated.exists(u -> u.equals(var))))
                    .map(f -> f.render(variables));
        }
    }

    private interface Command {
        <T> T receive(Function<Assignment, T> assignment, Function<Formula, T> formula);

        Pattern ASSIGNMENT = Pattern.compile("^(?<var>\\w+):(?<val>\\d+(?:\\.\\d+)?)$");
        Pattern FORMULA = Pattern.compile("^=(?<l>\\w+)(?<op>[+*\\-/])(?<r>\\w+)$");

        static Optional<Matcher> tryMatch(Pattern regex, String input) {
            return Optional.of(regex.matcher(input)).filter(Matcher::matches);
        }

        static Command parse(String s) {
            return Stream.<Supplier<Optional<Command>>>of(
                    () -> tryMatch(ASSIGNMENT, s).map(m -> new Assignment(m.group("var"), Double.parseDouble(m.group("val")))),
                    () -> tryMatch(FORMULA, s).map(m -> new Formula(m.group("l"), Operator.fromSymbol(m.group("op")), m.group("r"))))
                    .map(Supplier::get)
                    .filter(Optional::isPresent)
                    .findFirst()
                    .flatMap(Function.identity())
                    .orElseThrow(() -> new IllegalArgumentException("Bad command: " + s));
        }
    }

    private static final class Assignment implements Command {
        final String name;
        final double value;

        public Assignment(String name, double value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public <T> T receive(Function<Assignment, T> assigned, Function<Formula, T> defined) {
            return assigned.apply(this);
        }
    }

    enum Operator {
        PLUS("+", (a, b) -> a + b),
        MINUS("-", (a, b) -> a - b),
        TIMES("*", (a, b) -> a * b),
        DIV("/", (a, b) -> a / b),
        ;

        final String symbol;
        final DoubleBinaryOperator calc;

        Operator(String symbol, DoubleBinaryOperator calc) {
            this.symbol = symbol;
            this.calc = calc;
        }

        String render(Object left, Object right) {
            return left + symbol + right;
        }

        public static Operator fromSymbol(String symbol) {
            return Stream.of(values())
                    .filter(op -> op.symbol.equals(symbol))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(symbol));
        }
    }

    private static final class Formula implements Command {
        final String left, right;
        final Operator op;

        private Formula(String left, Operator op, String right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        List<String> variables() {
            return arrayList(left, right);
        }

        @Override
        public <T> T receive(Function<Assignment, T> assigned, Function<Formula, T> defined) {
            return defined.apply(this);
        }

        public String render(TreeMap<String, Double> variables) {
            Option<Double> maybeL = variables.get(left), maybeR = variables.get(right);
            return op.render(left, right) + "=" + maybeL.bind(l ->
                    maybeR.map(r -> op.calc.applyAsDouble(l, r)))
                    .map(String::valueOf)
                    .orSome(() -> op.render(
                            maybeL.map(String::valueOf).orSome("?"),
                            maybeR.map(String::valueOf).orSome("?")));
        }
    }
}
