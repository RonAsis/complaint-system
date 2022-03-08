package com.craft.externalmanagementsystemms.services.helpers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelperPage<T> extends PageImpl<T> {

	private static final long serialVersionUID = 1L;

	private int totalPages;
	private boolean last;
	private boolean first;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public HelperPage(@JsonProperty("content") List<T> content,
                      @JsonProperty("number") int number,
                      @JsonProperty("size") int size,
                      @JsonProperty("totalElements") Long totalElements,
                      @JsonProperty("pageable") JsonNode pageable,
                      @JsonProperty("last") boolean last,
                      @JsonProperty("totalPages") int totalPages,
                      @JsonProperty("sort") JsonNode sort,
                      @JsonProperty("first") boolean first,
                      @JsonProperty("numberOfElements") int numberOfElements) {

		super(content, PageRequest.of(number, size), totalElements);
		this.totalPages = totalPages;
		this.last = last;
		this.first = first;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public boolean isLast() {
		return last;
	}

	public boolean isFirst() {
		return first;
	}
	
}
