package org.molgenis.data.support;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.data.DataConverter;
import org.molgenis.data.Entity;
import org.molgenis.data.convert.DateToStringConverter;
import org.molgenis.data.meta.AttributeMetaData;
import org.molgenis.data.meta.EntityMetaData;

public abstract class AbstractEntity implements Entity
{
	private static final long serialVersionUID = 1L;

	@Override
	public Object getLabelValue()
	{
		AttributeMetaData labelAttribute = getEntityMetaData().getLabelAttribute();
		if (labelAttribute == null && getEntityMetaData().isAbstract())
		{
			return null;
		}

		String labelAttributeName = labelAttribute.getName();
		FieldTypeEnum dataType = labelAttribute.getDataType().getEnumType();
		switch (dataType)
		{
			case BOOL:
			case DECIMAL:
			case EMAIL:
			case ENUM:
			case HTML:
			case HYPERLINK:
			case INT:
			case LONG:
			case SCRIPT:
			case STRING:
			case TEXT:
				Object obj = get(labelAttributeName);
				return obj != null ? obj.toString() : null;
			case DATE:
			case DATE_TIME:
				Date date = getUtilDate(labelAttributeName);
				return new DateToStringConverter().convert(date);
			case CATEGORICAL:
			case XREF:
			case FILE:
				Entity refEntity = getEntity(labelAttributeName);
				return refEntity != null ? refEntity.getLabelValue() : null;
			case CATEGORICAL_MREF:
			case MREF:
				Iterable<Entity> refEntities = getEntities(labelAttributeName);
				if (refEntities != null)
				{
					StringBuilder strBuilder = new StringBuilder();
					for (Entity mrefEntity : refEntities)
					{
						if (strBuilder.length() > 0) strBuilder.append(',');
						strBuilder.append(mrefEntity.getLabelValue());
					}
					return strBuilder.toString();
				}
				return null;
			case COMPOUND:
				throw new RuntimeException("invalid label data type " + dataType);
			default:
				throw new RuntimeException("unsupported label data type " + dataType);
		}
	}

	@Override
	public String getString(String attributeName)
	{
		return DataConverter.toString(get(attributeName));
	}

	@Override
	public Integer getInt(String attributeName)
	{
		return DataConverter.toInt(get(attributeName));
	}

	@Override
	public Long getLong(String attributeName)
	{
		return DataConverter.toLong(get(attributeName));
	}

	@Override
	public Boolean getBoolean(String attributeName)
	{
		return DataConverter.toBoolean(get(attributeName));
	}

	@Override
	public Double getDouble(String attributeName)
	{
		return DataConverter.toDouble(get(attributeName));
	}

	@Override
	public java.sql.Date getDate(String attributeName)
	{
		return DataConverter.toDate(get(attributeName));
	}

	@Override
	public java.util.Date getUtilDate(String attributeName)
	{
		return DataConverter.toUtilDate(get(attributeName));
	}

	@Override
	public Timestamp getTimestamp(String attributeName)
	{
		return DataConverter.toTimestamp(get(attributeName));
	}

	@Override
	public Entity getEntity(String attributeName)
	{
		return DataConverter.toEntity(get(attributeName));
	}

	@Override
	public <E extends Entity> E getEntity(String attributeName, Class<E> clazz)
	{
		throw new UnsupportedOperationException("FIXME"); // FIXME
		//		Entity entity = getEntity(attributeName);
		//		return entity != null ? new ConvertingIterable<E>(clazz, Arrays.asList(entity)).iterator().next() : null;
	}

	@Override
	public Iterable<Entity> getEntities(String attributeName)
	{
		Iterable<Entity> entities = DataConverter.toEntities(get(attributeName));
		return entities != null ? entities : emptyList();
	}

	@Override
	public <E extends Entity> Iterable<E> getEntities(String attributeName, Class<E> clazz)
	{
		throw new UnsupportedOperationException("FIXME"); // FIXME
		//		Iterable<Entity> entities = getEntities(attributeName);
		//		return entities != null ? new ConvertingIterable<E>(clazz, entities) : emptyList();
	}

	public List<String> getList(String attributeName)
	{
		return DataConverter.toList(get(attributeName));
	}

	public List<Integer> getIntList(String attributeName)
	{
		return DataConverter.toIntList(get(attributeName));
	}

	@Override
	public Object getIdValue()
	{
		return get(getEntityMetaData().getIdAttribute().getName());
	}

	@Override
	public void setIdValue(Object id)
	{
		AttributeMetaData idAttr = getEntityMetaData().getIdAttribute();
		if (idAttr == null)
		{
			throw new IllegalArgumentException(format("Entity [%s] doesn't have an id attribute"));
		}
		set(idAttr.getName(), id);
	}

	@Override
	public Iterable<String> getAttributeNames()
	{
		return EntityMetaDataUtils.getAttributeNames(getEntityMetaData().getAtomicAttributes());
	}

	@Override
	public String toString()
	{
		EntityMetaData entityMetaData = getEntityMetaData();
		if (entityMetaData != null)
		{
			return String.format("%s=[%s]", entityMetaData.getName(), toString(entityMetaData.getAttributes()));
		}
		else
		{
			return getClass().getSimpleName() + "=[<missing entity meta data>]";
		}
	}

	public String toString(Iterable<AttributeMetaData> attrs)
	{
		return StreamSupport.stream(attrs.spliterator(), false).map(attr -> {
			String attrName = attr.getName();
			if (attr.getDataType().getEnumType() == FieldTypeEnum.COMPOUND)
			{
				return String.format("%s={%s}", attrName, toString(attr.getAttributeParts()));
			}
			else
			{
				return String.format("%s=%s", attrName, this.get(attrName));
			}
		}).collect(Collectors.joining(","));
	}
}
