#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function listDir(dir) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      const fullPath = join(dir, file);
      try {
        const stat = await fs.stat(fullPath);
        if (stat.isDirectory()) {
          console.log(`📁 ${fullPath}`);
          // Recurse one level
          const subfiles = await fs.readdir(fullPath);
          for (const sf of subfiles) {
             console.log(`  - ${sf}`);
          }
        } else {
          console.log(`📄 ${fullPath} (${stat.size} bytes)`);
        }
      } catch (e) {}
    }
  } catch (err) {}
}

console.log("Listing /tmp:");
await listDir('/tmp');

console.log("\nListing /home:");
await listDir('/home');

console.log("\nListing /opt:");
await listDir('/opt');
