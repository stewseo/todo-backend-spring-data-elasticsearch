package com.example.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;

// variant objects in the Java API Client are implementations of a “tagged union”: they contain the identifier (or tag) of the variant they hold and the value for that variant
public interface QueryBuilderUtils {

    public static Query termQuery(String field, String value){
        return Query.of(q -> q
                .term(t -> t // Returns documents that contain an exact term in a provided field.
                        .caseInsensitive(true)
                        .field(field)
                        .value(value))
        );
    }

    public static Query matchQuery(String field, String query) {
        return Query.of(q -> q
                .match(m -> m // Returns documents that match a provided text, number, date or boolean value. The provided text is analyzed before matching.
                        .field(field) // search field
                        .query(query) // search text
                )
        );
    }

}
