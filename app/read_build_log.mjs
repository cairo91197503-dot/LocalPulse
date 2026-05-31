#!/usr/bin/env zx
import { promises as fs } from 'fs';

try {
  const content = await fs.readFile('/app/build_output.log', 'utf8');
  console.log("=== BUILD LOG START ===");
  console.log(content.length > 5000 ? content.slice(-5000) : content);
  console.log("=== BUILD LOG END ===");
} catch (e) {
  console.log("Could not read build log:", e.message);
}
