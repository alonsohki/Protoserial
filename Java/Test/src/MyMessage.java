import com.conditionracer.protoserial.*;

@Message
public class MyMessage extends ParentMessage
{
    public Integer varIntegerNullable;
    public Integer varInteger;
    public Long varLong;
    @Unsigned public Integer varUint;
    public String varString;
    public Float varFloat;
    public OtherMessage varOther;

    @Required public String requiredField;
}
