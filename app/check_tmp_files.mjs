#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function listAll(dir) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      const fullPath = join(dir, file);
      const stat = await fs.stat(fullPath);
      if (stat.isDirectory()) {
        await listAll(fullPath);
      } else {
        console.log(`FILE: ${fullPath} (${stat.size} bytes)`);
      }
    }
  } catch (err) {}
}

console.log("Listing everything under /tmp/10697916401008908909:");
await listAll('/tmp/10697916401008908909');
