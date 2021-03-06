package com.thomasbarker.bullionprompt.xml.documents;

import lombok.Data;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType( XmlAccessType.FIELD )
@Data
public abstract class AbstractMessageDocument<T>
	implements MessageContainer<T>
{
	public abstract AbstractMessage getMessage();

	@XmlRootElement( name = "message" )
	@XmlAccessorType( XmlAccessType.FIELD )
    @Data
	public static abstract class AbstractMessage {

		@XmlAttribute protected String type;
		@XmlAttribute protected BigDecimal version;

		protected abstract String getRequiredType();
		protected abstract BigDecimal getRequiredVersion();

		final void afterUnmarshal( Unmarshaller unmarshaller, Object parent)
			throws JAXBException
		{
			if ( !type.startsWith( getRequiredType() ) ) {
				throw new JAXBException( "Type is " + type + " not " + getRequiredType() );
			}
			if ( 0 != version.compareTo( getRequiredVersion() ) ) {
				throw new JAXBException( "Version is " + version + " not " + getRequiredVersion() );
			}
		}

		@XmlElementWrapper( name = "errors" )
	    @XmlElement( name = "error" )
	    private List<String> errors = new ArrayList<String>();
	}

	@XmlElementWrapper( name = "errors" )
    @XmlElement( name = "error" )
    private List<String> errors = new ArrayList<String>();

	final void afterUnmarshal( Unmarshaller unmarshaller, Object parent)
		throws JAXBException
	{
		if ( getMessage().getType().endsWith( "_E" ) ) {
			getErrors().add( "BV API error envelope." );
		}

		getErrors().addAll( getMessage().getErrors() );

		fixUp();
	}

	// Allow safe extension of afterUnmarshal
	protected void fixUp() { ; }
}
