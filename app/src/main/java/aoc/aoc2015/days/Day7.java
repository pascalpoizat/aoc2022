package aoc.aoc2015.days;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import aoc.helpers.Day;
import aoc.helpers.LineReader;
import aoc.helpers.ListCreator;
import aoc.helpers.Pair;
import static aoc.helpers.Readers.*;

public class Day7 {

    // Does not support cycles in definitions
    // e.g., x -> x or x AND y -> z + z -> x

    private Day7() {
    }

    // LINE ::= INSTR -> WIRE
    // INSTR ::= VALUE
    // | NOT VALUE
    // | VALUE AND VALUE | VALUE OR VALUE | VALUE LSHIFT VALUE | VALUE RSHIFT VALUE
    // VALUE ::= NUMBER | WIRE

    public static class Context {
        private Map<Wire, Instruction> values;
        private Map<Wire, Optional<Integer>> memory;

        public Context() {
            this.values = new HashMap<>();
            reset();
        }

        public void reset() {
            this.memory = new HashMap<>();
        }

        public void set(Wire wire, Instruction value) {
            values.put(wire, value);
        }

        public Optional<Integer> value(Wire wire) {
            if (!memory.containsKey(wire)) {
                memory.put(wire, Optional.ofNullable(values.get(wire)).flatMap(inst -> inst.value(this)));
            }
            return memory.get(wire);
        }

        public Set<Wire> ids() {
            return values.keySet();
        }

    }

    public interface Instruction {
        Optional<Integer> value(Context c);
    }

    public static class INot implements Instruction {
        private Value instr;

        public INot(Value instr) {
            this.instr = instr;
        }

        @Override
        public Optional<Integer> value(Context c) {
            Optional<Integer> v = instr.value(c);
            if (v.isPresent())
                return Optional.ofNullable((int) (char) ~v.get());
            else
                return Optional.empty();
        }
    }

    public static class IAnd implements Instruction {
        private Value left;
        private Value right;

        public IAnd(Value left, Value right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Optional<Integer> value(Context c) {
            Optional<Integer> l = left.value(c);
            Optional<Integer> r = right.value(c);
            if (l.isPresent() && r.isPresent())
                return Optional.ofNullable(l.get() & r.get());
            else
                return Optional.empty();
        }
    }

    public static class IOr implements Instruction {
        private Value left;
        private Value right;

        public IOr(Value left, Value right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Optional<Integer> value(Context c) {
            Optional<Integer> l = left.value(c);
            Optional<Integer> r = right.value(c);
            if (l.isPresent() && r.isPresent())
                return Optional.ofNullable(l.get() | r.get());
            else
                return Optional.empty();
        }
    }

    public static class ILShift implements Instruction {
        private Value left;
        private Value right;

        public ILShift(Value left, Value right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Optional<Integer> value(Context c) {
            Optional<Integer> l = left.value(c);
            Optional<Integer> r = right.value(c);
            if (l.isPresent() && r.isPresent())
                return Optional.ofNullable(l.get() << r.get());
            else
                return Optional.empty();
        }
    }

    public static class IRShift implements Instruction {
        private Value left;
        private Value right;

        public IRShift(Value left, Value right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Optional<Integer> value(Context c) {
            Optional<Integer> l = left.value(c);
            Optional<Integer> r = right.value(c);
            if (l.isPresent() && r.isPresent())
                return Optional.ofNullable(l.get() >> r.get());
            else
                return Optional.empty();
        }
    }

    public interface Value extends Instruction {
    }

    public static class Number implements Value {
        private int value;

        public Number(Integer value) {
            this.value = value;
        }

        @Override
        public Optional<Integer> value(Context c) {
            return Optional.ofNullable(value);
        }
    }

    public static class Wire implements Value, Comparable<Wire> {
        private String wire;

        public Wire(String wire) {
            this.wire = wire;
        }

        @Override
        public Optional<Integer> value(Context c) {
            return c.value(this);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((wire == null) ? 0 : wire.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Wire other = (Wire) obj;
            if (wire == null) {
                if (other.wire != null)
                    return false;
            } else if (!wire.equals(other.wire))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return wire;
        }

        @Override
        public int compareTo(Wire w) {
            return wire.compareTo(w.wire);
        }
    }

    public static final LineReader<Number> readNumber = l -> integer.apply(l).map(Number::new);

    public static final LineReader<Wire> readWire = l -> id.apply(l).map(Wire::new);

    public static final LineReader<Value> createValue = l -> {
        Optional<Value> rtr = readNumber.apply(l).map(Value.class::cast);
        if (rtr.isEmpty()) {
            rtr = readWire.apply(l).map(Value.class::cast);
        }
        return rtr;
    };

    public static final ListCreator<Instruction> createAnd = ls -> {
        Optional<Value> left = createValue.apply(ls.get(0));
        Optional<Value> right = createValue.apply(ls.get(2));
        if (left.isPresent() && right.isPresent()) {
            return Optional.ofNullable(new IAnd(left.get(), right.get()));
        } else {
            return Optional.empty();
        }
    };

    public static final ListCreator<Instruction> createOr = ls -> {
        Optional<Value> left = createValue.apply(ls.get(0));
        Optional<Value> right = createValue.apply(ls.get(2));
        if (left.isPresent() && right.isPresent()) {
            return Optional.ofNullable(new IOr(left.get(), right.get()));
        } else {
            return Optional.empty();
        }
    };

    public static final ListCreator<Instruction> createLShift = ls -> {
        Optional<Value> left = createValue.apply(ls.get(0));
        Optional<Value> right = createValue.apply(ls.get(2));
        if (left.isPresent() && right.isPresent()) {
            return Optional.ofNullable(new ILShift(left.get(), right.get()));
        } else {
            return Optional.empty();
        }
    };

    public static final ListCreator<Instruction> createRShift = ls -> {
        Optional<Value> left = createValue.apply(ls.get(0));
        Optional<Value> right = createValue.apply(ls.get(2));
        if (left.isPresent() && right.isPresent()) {
            return Optional.ofNullable(new IRShift(left.get(), right.get()));
        } else {
            return Optional.empty();
        }
    };

    public static final ListCreator<Instruction> createNot = ls -> {
        if(ls.get(0).strip().equals("NOT")) {
            Optional<Value> instr = createValue.apply(ls.get(1));
            if (instr.isPresent()) {
                return Optional.ofNullable(new INot(instr.get()));
            }
        }
        return Optional.empty();
    };

    public static final ListCreator<Instruction> createBinary = ls -> {
        String instruction = ls.get(1);
        if (instruction.equals("AND")) {
            return createAnd.fromList(ls);
        } else if (instruction.equals("OR")) {
            return createOr.fromList(ls);
        } else if (instruction.equals("LSHIFT")) {
            return createLShift.fromList(ls);
        } else if (instruction.equals("RSHIFT")) {
            return createRShift.fromList(ls);
        } else {
            return Optional.empty();
        }
    };

    public static final ListCreator<Instruction> createInstruction = ls -> switch (ls.size()) {
        case 1 -> createValue.apply(ls.get(0)).map(Instruction.class::cast);
        case 2 -> createNot.fromList(ls);
        case 3 -> createBinary.fromList(ls);
        default -> Optional.empty();
    };

    public static final LineReader<Instruction> readInstruction = splitN(" ", createInstruction);

    public static final LineReader<Pair<Instruction, Wire>> readDefinition = split(" -> ",
            readInstruction, readWire, Pair::of);

    public static final Day day7a = ls -> {
        Context c = new Context();
        ls.stream()
                .map(readDefinition)
                .flatMap(Optional::stream)
                .forEach(line -> c.set(line.snd(), line.fst()));
        return c.value(new Wire("a")).map(i -> i.toString()).orElse("not found");
    };

    public static final Day day7b = ls -> {
        Context c = new Context();
        ls.stream()
                .map(readDefinition)
                .flatMap(Optional::stream)
                .forEach(line -> c.set(line.snd(), line.fst()));
        Optional<Integer> value = c.value(new Wire("a"));
        c.reset();
        c.set(new Wire("b"), new Number(value.get()));
        return c.value(new Wire("a")).map(i -> i.toString()).orElse("not found");
    };

}
