import com.conditionracer.protoserial.*;

@Message
public class MyMessage extends ParentMessage
{
    public Integer varIntegerNullable;
    @Unsigned public Short varUshort;
    public Short varShort;
    @Unsigned public Integer varUint;
    public Integer varInteger;
    @Unsigned public Long varUlong;
    public Long varLong;
    public Float varFloat;
    public Double varDouble;
    public String varString;
    public OtherMessage varOther;

    public Integer[] varRepeatedInt;
    public OtherMessage[] varRepeatedOther;

    @Required public String requiredField;
}
