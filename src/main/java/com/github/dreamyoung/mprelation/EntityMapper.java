package com.github.dreamyoung.mprelation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityMapper {

	/**
	 * (Optional) The entity class that is the target of the association. Optional
	 * only if the collection property is defined using Java generics. Must be
	 * specified otherwise.
	 *
	 * <p>
	 * Defaults to the parameterized type of the collection when defined using
	 * generics.
	 */
	Class<?> targetMapper() default void.class;

	/**
	 * (Optional) Whether the association should be lazily loaded or must be eagerly
	 * fetched. The EAGER strategy is a requirement on the persistence provider
	 * runtime that the associated entities must be eagerly fetched. The LAZY
	 * strategy is a hint to the persistence provider runtime.
	 */
	FetchType fetch() default FetchType.LAZY;

}
