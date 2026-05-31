#!/usr/bin/env zx
import { promises as fs } from 'fs';

try {
  const files = await fs.readdir('.', { withFileTypes: true });
  for (const file of files) {
    console.log(`${file.isDirectory() ? '📁' : '📄'} ${file.name}`);
  }
} catch (err) {
  console.error("Error:", err);
}
