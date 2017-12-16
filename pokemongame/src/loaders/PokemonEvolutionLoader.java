package loaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import thingFramework.Attribute;
import thingFramework.AttributeNotFoundException;

/**
 * Adds to each pokemon it's evolution if it has one, using data from evolution sheet
 * @author David O'Sullivan
 *
 */

