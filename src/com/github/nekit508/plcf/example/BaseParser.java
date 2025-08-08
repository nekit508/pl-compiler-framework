package com.github.nekit508.plcf.example;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.github.nekit508.plcf.lang.compiletime.parser.Parser;
import com.github.nekit508.plcf.lang.compiletime.parser.ParserFeature;
import com.github.nekit508.plcf.lang.compiletime.token.Token;
import com.github.nekit508.plcf.lang.exceptions.ParseFail;
import com.github.nekit508.plcf.lang.utils.ReusableStream;

public class BaseParser extends Parser<BaseContext> {
    public static ParserFeature<BaseContext, BaseTree.Map> mapFeature;
    public static ParserFeature<BaseContext, BaseTree.Key> keyFeature;
    public static ParserFeature<BaseContext, BaseTree.Value> valueFeature;
    public static ParserFeature<BaseContext, BaseTree.Array> arrayFeature;

    public static ParserFeature<BaseContext, BaseTree.Number> numberFeature;
    public static ParserFeature<BaseContext, BaseTree.Str> stringFeature;
    public static ParserFeature<BaseContext, BaseTree.Bool> boolFeature;

    static {
        keyFeature = new ParserFeature<>("key", parser -> {
            var ident = parser.accept(BaseTokenKind.IDENT);
            return new BaseTree.Key(BaseTreeKinds.KEY, ident.literal);
        });

        mapFeature = new ParserFeature<>("map", parser -> {
            var data = new ObjectMap<BaseTree.Key, BaseTree.Value>();

            parser.accept(OneSymbolTokenKind.L_BRACE);
            while (parser.probeNotAndRedoIf(true, OneSymbolTokenKind.R_BRACE)) {
                var key = parser.parse(keyFeature);
                parser.accept(OneSymbolTokenKind.COLON);
                var value = parser.parseAny(valueFeature);

                data.put(key, value);
            }

            return new BaseTree.Map(BaseTreeKinds.MAP, data);
        });

        valueFeature = new ParserFeature<>("value", parser -> {
            return parser.parseAny(
                    arrayFeature, numberFeature, stringFeature, boolFeature, mapFeature
            );
        });

        arrayFeature = new ParserFeature<>("array", parser -> {
            parser.accept(OneSymbolTokenKind.L_BRACKET);

            var out = new Seq<BaseTree.Value>();

            if (parser.probeNotAndRedoIf(true, OneSymbolTokenKind.R_BRACKET)) {
                do {
                    out.add(parser.parse(valueFeature));
                } while (parser.probeAndRedoIf(false, OneSymbolTokenKind.COMMA));
                parser.probe(OneSymbolTokenKind.R_BRACKET);
            }

            return new BaseTree.Array(BaseTreeKinds.ARRAY, null);
        });

        stringFeature = new ParserFeature<>("string", parser -> {
            var string = parser.accept(BaseTokenKind.STRING).literal;
            return new BaseTree.Str(BaseTreeKinds.STRING, string);
        });

        boolFeature = new ParserFeature<>("bool", parser -> {
            var literal = parser.accept(BaseTokenKind.BOOLEAN).literal;
            boolean value;

            if (literal.equals("true"))
                value = true;
            else if (literal.equals("false"))
                value = false;
            else throw new ParseFail("unknown boolean literal " + literal + '.');

            return new BaseTree.Bool(BaseTreeKinds.BOOL, value);
        });

        numberFeature = new ParserFeature<>("number", parser -> {
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
    public ParserFeature<BaseContext, BaseTree.Map> getRootFeature() {
        return mapFeature;
    }
}
