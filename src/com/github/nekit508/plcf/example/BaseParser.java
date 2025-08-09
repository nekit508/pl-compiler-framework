package com.github.nekit508.plcf.example;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.github.nekit508.plcf.lang.compiletime.parser.Parser;
import com.github.nekit508.plcf.lang.compiletime.parser.ParserRule;
import com.github.nekit508.plcf.lang.compiletime.token.Token;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

public class BaseParser extends Parser<BaseContext> {
    public static ParserRule<BaseParser, BaseTree.Map> mapRule;
    public static ParserRule<BaseParser, BaseTree.Key> keyRule;
    public static ParserRule<BaseParser, BaseTree.Value> valueRule;
    public static ParserRule<BaseParser, BaseTree.Array> arrayRule;

    public static ParserRule<BaseParser, BaseTree.Number> numberRule;
    public static ParserRule<BaseParser, BaseTree.Str> stringRule;
    public static ParserRule<BaseParser, BaseTree.Bool> boolRule;

    static {
        keyRule = new ParserRule<>("key", parser -> {
            var ident = parser.accept(BaseTokenKind.IDENT);
            return new BaseTree.Key(BaseTreeKinds.KEY, ident.literal);
        });

        mapRule = new ParserRule<>("map", parser -> {
            var data = new ObjectMap<BaseTree.Key, BaseTree.Value>();

            parser.accept(OneSymbolTokenKind.L_BRACE);
            while (parser.probeNotAndRedoIf(true, OneSymbolTokenKind.R_BRACE)) {
                var key = parser.parse(keyRule);
                parser.accept(OneSymbolTokenKind.COLON);
                var value = parser.parseAny(valueRule);

                data.put(key, value);
            }

            return new BaseTree.Map(BaseTreeKinds.MAP, data);
        });

        valueRule = new ParserRule<>("value", parser -> {
            return parser.parseAny(
                    arrayRule, numberRule, stringRule, boolRule, mapRule
            );
        });

        arrayRule = new ParserRule<>("array", parser -> {
            parser.accept(OneSymbolTokenKind.L_BRACKET);

            var out = new Seq<BaseTree.Value>();

            if (parser.probeNotAndRedoIf(true, OneSymbolTokenKind.R_BRACKET)) {
                do {
                    out.add(parser.parse(valueRule));
                } while (parser.probeAndRedoIf(false, OneSymbolTokenKind.COMMA));
                parser.probe(OneSymbolTokenKind.R_BRACKET);
            }

            return new BaseTree.Array(BaseTreeKinds.ARRAY, null);
        });

        stringRule = new ParserRule<>("string", parser -> {
            var string = parser.accept(BaseTokenKind.STRING).literal;
            return new BaseTree.Str(BaseTreeKinds.STRING, string);
        });

        boolRule = new ParserRule<>("bool", parser -> {
            var literal = parser.accept(BaseTokenKind.BOOLEAN).literal;
            boolean value;

            if (literal.equals("true"))
                value = true;
            else if (literal.equals("false"))
                value = false;
            else throw new ParseFail("unknown boolean literal " + literal + '.');

            return new BaseTree.Bool(BaseTreeKinds.BOOL, value);
        });

        numberRule = new ParserRule<>("number", parser -> {
            var literal = parser.accept(BaseTokenKind.NUMBER).literal;
            try {
                Object out = null;

                String numberBody = literal.substring(0, literal.length() - 1);
                char numberType = literal.charAt(literal.length() - 1);

                if (numberType == 'i' || numberType == 'I')
                    out = Integer.parseInt(numberBody);
                else if (numberType == 'f' || numberType == 'F')
                    out = Float.parseFloat(numberBody);
                else if (numberType == 's' || numberType == 'S')
                    out = Short.parseShort(numberBody);
                else if (numberType == 'l' || numberType == 'L')
                    out = Long.parseLong(numberBody);
                else if (numberType == 'd' || numberType == 'D')
                    out = Double.parseDouble(numberBody);
                else if (numberType == 'b' || numberType == 'B')
                    out = Byte.parseByte(numberBody);

                if (out == null)
                    throw new ParseFail("Wrong number type " + numberType + ".");

                return new BaseTree.Number(BaseTreeKinds.NUMBER, out);
            } catch (NumberFormatException e) {
                throw new ParseFail("Wrong number body in " + literal + ".");
            }
        });
    }

    public BaseParser(ReusableStream<Token> stream, BaseContext context) {
        super(stream, context);
    }

    @Override
    public ParserRule<BaseParser, BaseTree.Map> getRootRule() {
        return mapRule;
    }
}
