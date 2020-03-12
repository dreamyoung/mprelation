package com.github.dreamyoung.mprelation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Target({})
@Retention(RUNTIME)
public @interface ForeignKey {
	/**
	 * (Optional) The name of the foreign key constraint.  Defaults to a provider-generated name.
	 *
	 * @return The foreign key name
	 */
	String name() default "";

	/**
	 * (Optional) The foreign key constraint definition.  Default is provider defined.  If the value of
	 * disableForeignKey is true, the provider must not generate a foreign key constraint.
	 *
	 * @return The foreign key definition
	 */
	String foreignKeyDefinition() default "";

	/**
	 * (Optional) Used to specify whether a foreign key constraint should be generated when schema generation is in effect.
	 */
	ConstraintMode value() default ConstraintMode.CONSTRAINT;
}
