package com.github.dreamyoung.mprelation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, FIELD})
@Retention(RUNTIME)

public @interface JoinTable {

    /**
     * (Optional) The name of the join table.
     *
     * <p> Defaults to the concatenated names of
     * the two associated primary entity tables,
     * separated by an underscore.
     */
    String name() default "";

    /** (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
    String catalog() default "";

    /** (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    String schema() default "";

    /**
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity owning the
     * association. (I.e. the owning side of
     * the association).
     *
     * <p> Uses the same defaults as for {@link JoinColumn}.
     */
    JoinColumn[] joinColumns() default {};

    /**
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity that does
     * not own the association. (I.e. the
     * inverse side of the association).
     *
     * <p> Uses the same defaults as for {@link JoinColumn}.
     */
    JoinColumn[] inverseJoinColumns() default {};

    /**
     * (Optional) Unique constraints that are
     * to be placed on the table. These are
     * only used if table generation is in effect.
     * <p> Defaults to no additional constraints.
     */
    //UniqueConstraint[] uniqueConstraints() default {};

	/**
	 * (Optional) Indexes for the table. These are only used if table generation is in effect.
	 *
	 * @return The indexes
	 */
	//Index[] indexes() default {};

	/**
	 * (Optional) Used to specify or control the generation of a foreign key constraint for the columns
	 * corresponding to the joinColumns element when table generation is in effect.
	 *
	 * @since Java Persistence 2.1
	 */
	ForeignKey foreignKey() default @ForeignKey(ConstraintMode.PROVIDER_DEFAULT);

	/**
	 * (Optional) Used to specify or control the generation of a foreign key constraint for the columns
	 * corresponding to the inverseJoinColumns element when table generation is in effect.
	 *
	 * @since Java Persistence 2.1
	 */
	ForeignKey inverseForeignKey() default @ForeignKey(ConstraintMode.PROVIDER_DEFAULT);

	Class<?> entityClass() default void.class;
	
	Class<?> targetMapper() default void.class;
}

