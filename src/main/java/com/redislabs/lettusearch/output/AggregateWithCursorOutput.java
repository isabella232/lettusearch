package com.redislabs.lettusearch.output;

import com.redislabs.lettusearch.aggregate.AggregateWithCursorResults;

import io.lettuce.core.codec.RedisCodec;

public class AggregateWithCursorOutput<K, V> extends AggregateOutput<K, V, AggregateWithCursorResults<K, V>> {

	private int count = -1;

	public AggregateWithCursorOutput(RedisCodec<K, V> codec) {
		super(codec, new AggregateWithCursorResults<>());
	}

	@Override
	public void set(long integer) {
		if (getMapCount() == output.size()) {
			output.setCursor(integer);
		} else {
			super.set(integer);
		}
	}

	@Override
	public void multi(int count) {
		if (this.count == -1) {
			this.count = count;
		} else {
			super.multi(count);
		}
	}

}
