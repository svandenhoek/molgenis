package org.molgenis.data.mysql;

import static org.testng.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.molgenis.MolgenisFieldTypes;
import org.molgenis.data.EditableEntityMetaData;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.molgenis.data.support.MapEntity;
import org.testng.Assert;

/** Test for MolgenisFieldTypes.DATETIME */
public class MysqlRepositoryDatetimeTest extends MysqlRepositoryAbstractDatatypeTest
{
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Override
	public EntityMetaData createMetaData()
	{
		EditableEntityMetaData entityMetaData = new DefaultEntityMetaData("DatetimeTest").setLabel("Datetime Test");
		entityMetaData.setIdAttribute("col1");
		entityMetaData.addAttribute("col1").setDataType(MolgenisFieldTypes.DATETIME).setNillable(false);
		entityMetaData.addAttribute("col2").setDataType(MolgenisFieldTypes.DATETIME);
		entityMetaData.addAttribute("col3").setDataType(MolgenisFieldTypes.DATETIME).setDefaultValue("01-01-2014");
		return entityMetaData;
	}

	@Override
	public String createSql()
	{
		return "CREATE TABLE IF NOT EXISTS `DatetimeTest`(`col1` DATETIME NOT NULL, `col2` DATETIME, `col3` DATETIME, PRIMARY KEY (`col1`)) ENGINE=InnoDB;";
	}

	@Override
	public Entity createTestEntity() throws ParseException
	{
		Entity e = new MapEntity();
		e.set("col1", "2012-03-13 23:59:33");
		e.set("col2", sdf.parse("2013-02-09 13:12:11"));
		return e;
	}

	@Override
	public void verifyTestEntity(Entity e) throws Exception
	{
		assertEquals(e.get("col1"), sdf.parse("2012-03-13 23:59:33"));
		assertEquals(e.get("col2"), sdf.parse("2013-02-09 13:12:11"));
		Assert.assertNull(e.get("col3")); // default value should NOT be set by the repository, for then the user cannot
											// override it to be NULL in a form.
	}
}