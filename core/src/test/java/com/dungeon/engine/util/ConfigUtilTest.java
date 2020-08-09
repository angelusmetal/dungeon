package com.dungeon.engine.util;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.resource.LoadingException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ConfigUtilTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	@Test public void testGetInteger() {
		Config config = ConfigFactory.parseString("{ key = 1234 }");
		Optional<Integer> value = ConfigUtil.getInteger(config, "key");
		assertTrue(value.isPresent());
		assertEquals(1234, value.get().intValue());
	}

	@Test public void testGetIntegerMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<Integer> value = ConfigUtil.getInteger(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireInteger() {
		Config config = ConfigFactory.parseString("{ key = 1234 }");
		int value = ConfigUtil.requireInteger(config, "key");
		assertEquals(1234, value);
	}

	@Test public void testRequireIntegerMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireInteger(config, "key");
	}

	@Test public void testGetLong() {
		Config config = ConfigFactory.parseString("{ key = 1234 }");
		Optional<Long> value = ConfigUtil.getLong(config, "key");
		assertTrue(value.isPresent());
		assertEquals(1234L, value.get().longValue());
	}

	@Test public void testGetLongMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<Long> value = ConfigUtil.getLong(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireLong() {
		Config config = ConfigFactory.parseString("{ key = 1234 }");
		long value = ConfigUtil.requireLong(config, "key");
		assertEquals(1234L, value);
	}

	@Test public void testRequireLongMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireLong(config, "key");
	}

	@Test public void testGetFloat() {
		Config config = ConfigFactory.parseString("{ key = 1234.56 }");
		Optional<Float> value = ConfigUtil.getFloat(config, "key");
		assertTrue(value.isPresent());
		assertEquals(1234.56f, value.get(), 0f);
	}

	@Test public void testGetFloatMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<Float> value = ConfigUtil.getFloat(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireFloat() {
		Config config = ConfigFactory.parseString("{ key = 1234 }");
		float value = ConfigUtil.requireFloat(config, "key");
		assertEquals(1234f, value, 0f);
	}

	@Test public void testRequireFloatMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireFloat(config, "key");
	}

	@Test public void testGetDouble() {
		Config config = ConfigFactory.parseString("{ key = 1234.56 }");
		Optional<Double> value = ConfigUtil.getDouble(config, "key");
		assertTrue(value.isPresent());
		assertEquals(1234.56d, value.get(), 0d);
	}

	@Test public void testGetDoubleMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<Double> value = ConfigUtil.getDouble(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireDouble() {
		Config config = ConfigFactory.parseString("{ key = 1234.56 }");
		double value = ConfigUtil.requireDouble(config, "key");
		assertEquals(1234.56d, value, 0d);
	}

	@Test public void testRequireDoubleMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireDouble(config, "key");
	}

	@Test public void testGetBoolean() {
		Config config = ConfigFactory.parseString("{ key = true }");
		Optional<Boolean> value = ConfigUtil.getBoolean(config, "key");
		assertTrue(value.isPresent());
		assertTrue(value.get());
	}

	@Test public void testGetBooleanMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<Boolean> value = ConfigUtil.getBoolean(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireBoolean() {
		Config config = ConfigFactory.parseString("{ key = true }");
		boolean value = ConfigUtil.requireBoolean(config, "key");
		assertTrue(value);
	}

	@Test public void testRequireBooleanMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireBoolean(config, "key");
	}

	@Test public void testGetString() {
		Config config = ConfigFactory.parseString("{ key = string }");
		Optional<String> value = ConfigUtil.getString(config, "key");
		assertTrue(value.isPresent());
		assertEquals("string", value.get());
	}

	@Test public void testGetStringMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<String> value = ConfigUtil.getString(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireString() {
		Config config = ConfigFactory.parseString("{ key = string }");
		String value = ConfigUtil.requireString(config, "key");
		assertEquals("string", value);
	}

	@Test public void testRequireStringMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireString(config, "key");
	}

	@Test public void testGetIntList() {
		Config config = ConfigFactory.parseString("{ key = [1, 2, 3] }");
		Optional<List<Integer>> value = ConfigUtil.getIntList(config, "key");
		assertTrue(value.isPresent());
		assertEquals(Arrays.asList(1, 2 ,3), value.get());
	}

	@Test public void testGetIntListMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<List<Integer>> value = ConfigUtil.getIntList(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireIntList() {
		Config config = ConfigFactory.parseString("{ key = [1, 2, 3] }");
		List<Integer> value = ConfigUtil.requireIntList(config, "key");
		assertEquals(Arrays.asList(1, 2, 3), value);
	}

	@Test public void testRequireIntListgMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireIntList(config, "key");
	}

	@Test public void testGetStringList() {
		Config config = ConfigFactory.parseString("{ key = [a, b, c] }");
		Optional<List<String>> value = ConfigUtil.getStringList(config, "key");
		assertTrue(value.isPresent());
		assertEquals(Arrays.asList("a", "b" ,"c"), value.get());
	}

	@Test public void testGetStringListMissing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<List<String>> value = ConfigUtil.getStringList(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireStringList() {
		Config config = ConfigFactory.parseString("{ key = [a, b, c] }");
		List<String> value = ConfigUtil.requireStringList(config, "key");
		assertEquals(Arrays.asList("a", "b" ,"c"), value);
	}

	@Test public void testRequireStringListMissing() {
		exception.expect(ConfigException.Missing.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireStringList(config, "key");
	}

	@Test public void testGetVector2() {
		Config config = ConfigFactory.parseString("{ key = [12, 34] }");
		Optional<Vector2> value = ConfigUtil.getVector2(config, "key");
		assertTrue(value.isPresent());
		assertEquals(new Vector2(12, 34), value.get());
	}

	@Test public void testGeVector2Missing() {
		Config config = ConfigFactory.parseString("{  }");
		Optional<Vector2> value = ConfigUtil.getVector2(config, "key");
		assertFalse(value.isPresent());
	}

	@Test public void testRequireVector2List() {
		Config config = ConfigFactory.parseString("{ key = [12, 34] }");
		Vector2 value = ConfigUtil.requireVector2(config, "key");
		assertEquals(new Vector2(12, 34), value);
	}

	@Test public void testRequireVector2Missing() {
		exception.expect(LoadingException.class);
		Config config = ConfigFactory.parseString("{  }");
		ConfigUtil.requireVector2(config, "key");
	}

}
