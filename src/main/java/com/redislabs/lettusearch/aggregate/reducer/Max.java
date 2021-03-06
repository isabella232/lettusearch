package com.redislabs.lettusearch.aggregate.reducer;

import static com.redislabs.lettusearch.protocol.CommandKeyword.MAX;

import com.redislabs.lettusearch.protocol.RediSearchCommandArgs;

import lombok.Builder;

public class Max extends AbstractPropertyReducer {

	@Builder
	private Max(String as, String property) {
		super(as, property);
	}

	@Override
	protected <K, V> void buildFunction(RediSearchCommandArgs<K, V> args, String property) {
		args.add(MAX);
		args.add(1);
		args.addProperty(property);
	}

}
