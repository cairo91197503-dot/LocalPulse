#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function scan(dir, indent = "") {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      const p = join(dir, file);
      const stat = await fs.stat(p);
      if (stat.isDirectory()) {
        console.log(`${indent}[DIR] ${file}`);
        await scan(p, indent + "  ");
      } else {
        console.log(`${indent}[FILE] ${file} (${stat.size} bytes)`);
      }
    }
  } catch (err) {}
}

console.log("=== SCANNING app/build/tmp ===");
await scan('app/build/tmp');
