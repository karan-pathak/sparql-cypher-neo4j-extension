package org.dbis.server;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.internal.helpers.collection.MapUtil;

import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.cypherdsl.core.renderer.Renderer;

// tag::ColleaguesCypherExecutionResource[]
@Path("/translate")
public class TranslateSparql
{
    private final ObjectMapper objectMapper;
    private DatabaseManagementService dbms;

    public TranslateSparql( @Context DatabaseManagementService dbms )
    {
        this.dbms = dbms;
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Path("/sparql")
    public Response benchmark( )
    {
//        final Map<String, Object> params = MapUtil.map( "personName", personName );

        StreamingOutput stream = new StreamingOutput()
        {
            @Override
            public void write( OutputStream os ) throws IOException, WebApplicationException
            {
                JsonGenerator jg = objectMapper.getJsonFactory().createJsonGenerator( os, JsonEncoding.UTF8 );
                jg.writeStartObject();
                jg.writeFieldName( "benchmark_results" );
                jg.writeStartArray();

                long startTime = System.currentTimeMillis();
                final GraphDatabaseService graphDb = dbms.database( "neo4j" );
                try ( Transaction tx = graphDb.beginTx();
                      Result result = tx.execute( sparqlQuery() ) )
                {
//                    while ( result.hasNext() )
//                    {
//                        Map<String,Object> row = result.next();
//                        jg.writeString( ((Node) row.get( "colleague" )).getProperty( "name" ).toString() );
//                    }
                    long endTime = System.currentTimeMillis();
                    jg.writeString( String.valueOf(result.stream().count()) );
                    jg.writeString( String.valueOf((endTime - startTime)) );
                    tx.commit();
                }

                jg.writeEndArray();
                jg.writeEndObject();
                jg.flush();
                jg.close();
            }
        };

        return Response.ok().entity( stream ).type( MediaType.APPLICATION_JSON ).build();
    }

    private String sparqlQuery()
    {
        final String queryString = "select ?a where { ?a e:ns1__follows ?b . ?b e:ns1__likes ?c . ?c e:ns4__hasReview ?d . ?d e:ns4__reviewer ?e .}";
        List<Statement> cypherStatements = SparqlToCypherCompiler.convert(queryString);
        Renderer cypherRenderer = Renderer.getDefaultRenderer();
        return cypherRenderer.render(cypherStatements.get(0));
    }
}
// end::ColleaguesCypherExecutionResource[]