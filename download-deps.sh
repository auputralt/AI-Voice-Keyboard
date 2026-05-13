#!/bin/bash
# Download sherpa-onnx AAR to app/libs/
# Run this once before building: ./download-deps.sh

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIBS_DIR="$SCRIPT_DIR/app/libs"
AAR_FILE="$LIBS_DIR/sherpa-onnx.aar"
AAR_URL="https://github.com/k2-fsa/sherpa-onnx/releases/download/v1.13.1/sherpa-onnx-1.13.1.aar"

if [ -f "$AAR_FILE" ]; then
    echo "sherpa-onnx.aar already exists, skipping."
    exit 0
fi

mkdir -p "$LIBS_DIR"
echo "Downloading sherpa-onnx AAR (53MB)..."
curl -L -o "$AAR_FILE" "$AAR_URL"
echo "Done. Saved to $AAR_FILE"
