package pl.twyszomirski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping( path = "/demo" )
public class HelloController
{
    @Autowired
    private LogEntryRepository logEntryRepository;

    @GetMapping( path = "/hello" )
    public @ResponseBody
    String getHello( HttpServletRequest request )
    {
        LogEntry logEntry = new LogEntry();
        logEntry.setAddress( request.getRemoteAddr() );
        logEntry.setDate( new Date() );
        logEntryRepository.save( logEntry );
        return "Hello there, you are " + logEntryRepository.count() + " visitor";
    }

    @GetMapping( path = "/all" )
    public @ResponseBody
    Iterable< LogEntry > getAllEntries()
    {
        return logEntryRepository.findAll();
    }
}
