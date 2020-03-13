package com.github.dreamyoung.mprelation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface JoinColumns {

    /**
     * The join columns that map the relationship.
     */
    JoinColumn[] value();

	/**
	 * (Optional) The foreign key constraint specification for the join columns. This is used only if table
	 * generation is in effect.  Default is provider defined.
	 *
	 * @return The foreign key specification
	 */
	ForeignKey foreignKey() default @ForeignKey();
}
