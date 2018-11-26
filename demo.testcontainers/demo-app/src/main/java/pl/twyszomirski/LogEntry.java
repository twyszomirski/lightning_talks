package pl.twyszomirski;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "log_entry")
public class LogEntry
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer id;

    @Temporal( TemporalType.DATE )
    private Date date;

    private String address;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer aId )
    {
        id = aId;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate( Date aDate )
    {
        date = aDate;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String aAddress )
    {
        address = aAddress;
    }
}

