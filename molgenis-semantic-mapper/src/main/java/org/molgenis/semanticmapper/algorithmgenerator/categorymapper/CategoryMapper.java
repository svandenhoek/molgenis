package org.molgenis.semanticmapper.algorithmgenerator.categorymapper;

import java.util.List;
import org.molgenis.semanticmapper.algorithmgenerator.bean.Category;
import org.molgenis.semanticmapper.algorithmgenerator.rules.CategoryMatchQuality;
import org.molgenis.semanticmapper.algorithmgenerator.rules.CategoryRule;

public abstract class CategoryMapper {
  protected final List<CategoryRule> rules;

  public CategoryMapper(List<CategoryRule> rules) {
    this.rules = rules;
  }

  public abstract Category findBestCategoryMatch(
      Category sourceCategory, List<Category> targetCategories);

  public abstract CategoryMatchQuality<?> applyCustomRules(
      Category sourceCategory, Category targetCategory);
}
